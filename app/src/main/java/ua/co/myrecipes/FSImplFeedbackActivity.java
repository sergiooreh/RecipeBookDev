/*
package ua.co.myrecipes;
*/
/*
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.remote.data.Invoice;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailInfo;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails;
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailsLocationInfoData;
import com.bykea.pk.partner.dal.source.remote.response.BookingUpdated;
import com.bykea.pk.partner.dal.source.remote.response.ConcludeJobBadResponse;
import com.bykea.pk.partner.dal.source.remote.response.FeedbackInvoiceResponse;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.databinding.ActivityFsImplFeedbackBinding;
import com.bykea.pk.partner.models.response.BatchBooking;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.common.LastAdapter;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryMsgsSpinnerAdapter;
import com.bykea.pk.partner.ui.nodataentry.BatchNaKamiyabDialog;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Util;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient;
import com.bykea.pk.partner.utils.extensions.BitmapExtKt;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.Extras.DELIVERY_DETAILS_OBJECT;
import static com.bykea.pk.partner.utils.Constants.RequestCode.RC_ADD_EDIT_DELIVERY_DETAILS;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.MART_LESS_THAN_TWO;
import static com.bykea.pk.partner.utils.Constants.ServiceCode.NEW_BATCH_DELIVERY_COD;

public class FSImplFeedbackActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFsImplFeedbackBinding binder;

    private int selectedMsgPosition = NumberUtils.INTEGER_ZERO;
    private ArrayList<Invoice> invoiceData = new ArrayList<>();

    private String totalCharges = StringUtils.EMPTY, lastKhareedariAmount = StringUtils.EMPTY;
    private int PARTNER_TOP_UP_NEGATIVE_LIMIT, AMOUNT_LIMIT, PARTNER_TOP_UP_POSITIVE_LIMIT;
    private JobsRepository repo;

    private int driverWallet;
    private boolean isJobSuccessful = true;
    private LastAdapter<Invoice> invoiceAdapter;

    private File photoImageFile;
    private Uri photoImageUri;
    private Bitmap photoBitmap;

    private boolean isNewBatchFlow;
    private int batchServiceCode;
    private String batchId;
    private boolean isRerouteCreated = false;
    private boolean mLastReturnRunBooking;
    private ArrayList<Invoice> batchInvoiceList;

    private boolean isBykeaCashType, isDeliveryType, isOfflineDeliveryType, isPurchaseType, isPaid;
    private boolean isAtmType;
    private NormalCallData callData;
    private DeliveryDetails reRouteDeliveryDetails;
    private boolean returnRunOnBooking;
    private BatchBooking trip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this, R.layout.activity_fs_impl_feedback);
        binder.setOnClickListener(this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        repo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
        try {
            driverWallet = Integer.parseInt(((DriverPerformanceResponse) AppPreferences.getObjectFromSharedPref(DriverPerformanceResponse.class)).getData().getTotalBalance());
        } catch (Exception e) {
            driverWallet = -1;
        }
        initViews();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Dialogs.INSTANCE.showLocationSettings(this, Permissions.LOCATION_PERMISSION);
        EventBus.getDefault().post(Constants.Broadcast.UPDATE_FOREGROUND_NOTIFICATION);
        updateScroll();
        updateInvoice();
        createFileAndUriForPhoto();
        setupTextChangeObservableForAmountText();
    }

    /**
     * this method will update the invoice details of the current booking,
     * will populate the recycler view's adapter
     *//*

    private void updateInvoice() {
        if (AppPreferences.getDriverSettings() == null ||
                AppPreferences.getDriverSettings().getData() == null ||
                StringUtils.isBlank(AppPreferences.getDriverSettings().getData().getFeedbackInvoiceListingUrl())) {
            Utils.appToast(getString(R.string.settings_are_not_updated));
            return;
        }

        repo.getInvoiceDetails(AppPreferences.getDriverSettings().getData().getFeedbackInvoiceListingUrl(),
                callData.getTripId(), new JobsDataSource.GetInvoiceCallback() {
                    @Override
                    public void onInvoiceDataLoaded(@NotNull FeedbackInvoiceResponse bookingDetailResponse) {
                        FSImplFeedbackActivity.this.invoiceData = bookingDetailResponse.getData();
                        updateAdapter();
                    }

                    @Override
                    public void onInvoiceDataFailed(@Nullable String errorMessage) {
                        Utils.appToast(errorMessage);
                    }
                });
    }

    private void updateAdapter() {
        ArrayList<Invoice> filtered = new ArrayList<>();
        for (Invoice invoice : invoiceData) {
            if (invoice.getDeliveryStatus() == null) {
                filtered.add(invoice);
            } else if (selectedMsgPosition == NumberUtils.INTEGER_ZERO && invoice.getDeliveryStatus() == NumberUtils.INTEGER_ONE) {
                filtered.add(invoice);
            } else if (selectedMsgPosition != NumberUtils.INTEGER_ZERO && invoice.getDeliveryStatus() != NumberUtils.INTEGER_ONE) {
                filtered.add(invoice);
            }
        }
        if (!(mLastReturnRunBooking && containsCodBooking())) updateTotal(filtered);
        invoiceAdapter.setItems(filtered);
    }

    */
/**
     * this will update ui for batch
     *
     * @param isKamiyabDelivery flag to check is kamiyab
     *//*

    private void handleInputInfoForBatch(boolean isKamiyabDelivery) {
        if (!isNewBatchFlow) return;
        binder.llReceiverInfo.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        binder.llTotal.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        binder.callerRb.setVisibility(isKamiyabDelivery ? View.VISIBLE : View.GONE);
        binder.llbatchNaKamiyabDelivery.setVisibility(!isKamiyabDelivery ? View.VISIBLE : View.GONE);
        binder.imageViewAddDelivery.setVisibility(!isKamiyabDelivery ? View.VISIBLE : View.GONE);
        binder.llFailureDelivery.setVisibility(View.GONE);
        binder.feedbackBtn.setEnabled(isKamiyabDelivery);
        if (reRouteDeliveryDetails != null) {
            onRerouteCreated(reRouteDeliveryDetails);
        }
    }

    */
/**
     * This method listens for touch on receivedAmountEt and moves scrollview to bottom
     *//*

    private void updateScroll() {
        moveScrollViewToBottom();
        binder.receivedAmountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //moveScrollViewToBottom();
                return false;
            }
        });
        binder.etReceiverName.requestFocus();
    }

    */
/**
     * This method scrolls down scroll view when it's ready
     *//*

    private void moveScrollViewToBottom() {
        binder.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binder.scrollView.fullScroll(View.FOCUS_DOWN);
                binder.scrollView.clearFocus();
                binder.scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setupTextChangeObservableForAmountText() {
        RxTextView.afterTextChangeEvents(binder.receivedAmountEt)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(textViewAfterTextChangeEvent -> afterReceivedAmountTextChanged(textViewAfterTextChangeEvent.editable()))
                .subscribe();
    }

    private void afterReceivedAmountTextChanged(Editable editable) {
        if (StringUtils.isNotBlank(editable) && StringUtils.isNotBlank(totalCharges)) {
            if (editable.toString().matches(Constants.REG_EX_DIGIT)) {
                if (Utils.isNewBatchService(batchServiceCode)) {
                    return;
                }
                if (driverWallet <= PARTNER_TOP_UP_NEGATIVE_LIMIT
                        && Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT + Constants.DIGIT_ONE) &&
                        !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
                    //WHEN THE WALLET IS LESS THAN ZERO, RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP NEGATIVE LIMIT)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT)));
                } else if ((driverWallet > PARTNER_TOP_UP_NEGATIVE_LIMIT && driverWallet < PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                        Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + driverWallet + Constants.DIGIT_ONE) &&
                        !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
                    //WHEN THE WALLET IS GREATER THAN ZERO BUT LESS THAN THE MAX POSITIVE TOP UP LIMIT,
                    //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND WALLET)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + driverWallet)));
                } else if ((Util.INSTANCE.isBykeaCashJob(callData.getServiceCode()) || driverWallet >= PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                        Integer.parseInt(editable.toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT + Constants.DIGIT_ONE)) {
                    //WHEN THE WALLET IS GREATER THAN MAX POSITIVE TOP UP LIMIT,
                    //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP POSITIVE LIMIT)
                    setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT)));
                } else if (Integer.parseInt(editable.toString()) >= (AMOUNT_LIMIT + Constants.DIGIT_ONE)) {
                    setEtError(getString(R.string.amount_error, AMOUNT_LIMIT));
                }
            } else {
                Utils.appToast(getString(R.string.invalid_amout));
            }
        }
    }


    private void initViews() {
        invoiceAdapter = new LastAdapter<>(R.layout.adapter_booking_detail_invoice, item -> {
        });
        binder.invoiceRecyclerView.setAdapter(invoiceAdapter);
        binder.invoiceRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        initCallData();
        isBykeaCashType = Util.INSTANCE.isBykeaCashJob(callData.getServiceCode());
        isDeliveryType = Utils.isDeliveryService(callData.getCallType());
        isAtmType = Utils.isAtmBooking(callData.getServiceCode());

        isOfflineDeliveryType = callData.getServiceCode() != null && callData.getServiceCode() == Constants.ServiceCode.OFFLINE_DELIVERY;
        isPurchaseType = Utils.isPurchaseService(callData.getCallType(), callData.getServiceCode());
        binder.etReceiverMobileNo.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        binder.receivedAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        binder.tvTripId.setText(callData.getTripNo());
        if (StringUtils.isNotBlank(callData.getTotalFare())) {
            totalCharges = callData.getTotalFare();
        }
        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
        AMOUNT_LIMIT = AppPreferences.getSettings().getSettings().getAmount_limit();
        PARTNER_TOP_UP_POSITIVE_LIMIT = AppPreferences.getSettings().getSettings().getPartnerTopUpLimitPositive();
        binder.startAddressTv.setText(callData.getStartAddress());
        binder.endAddressTv.setText((StringUtils.isBlank(callData.getEndAddress()) ? "N/A" : callData.getEndAddress()));

        if (Utils.isNewBatchService(batchServiceCode)) {
            binder.etReceiverName.setHint(R.string.consignees_name);

            boolean hasCodBooking = containsCodBooking();

            if (mLastReturnRunBooking && hasCodBooking) {
                binder.tvTotalRakmLabel.setTextSize(getResources().getDimension(R.dimen._11sdp));
                binder.ivBatchInfo.setVisibility(View.VISIBLE);
                if (AppPreferences.getDriverSettings() == null ||
                        AppPreferences.getDriverSettings().getData() == null ||
                        StringUtils.isBlank(AppPreferences.getDriverSettings().getData().getBatchBookingInvoiceUrl())) {
                    Utils.appToast(getString(R.string.settings_are_not_updated));
                    return;
                }
                repo.getReturnRunBatchInvoice(AppPreferences.getDriverSettings().getData().getBatchBookingInvoiceUrl(),
                        batchId, new JobsDataSource.GetInvoiceCallback() {

                            @Override
                            public void onInvoiceDataLoaded(@NotNull FeedbackInvoiceResponse feedbackInvoiceResponse) {
                                batchInvoiceList = feedbackInvoiceResponse.getData();
                                Dialogs.INSTANCE.showReturnRunInvoice(FSImplFeedbackActivity.this, batchInvoiceList, null);
                                binder.receivedAmountEt.setHint("Suggested Rs. " + updateTotal(batchInvoiceList));
                            }

                            @Override
                            public void onInvoiceDataFailed(@Nullable String errorMessage) {
                                Utils.appToast(errorMessage);
                            }
                        });
            }
        }
        if (isBykeaCashType) {
            updateUIBykeaCash();
        } else if (isAtmType) {
            updateUIForAtmBooking();
        } else if (isDeliveryType || isOfflineDeliveryType) {
            updateUIICODelivery();
        } else if (isPurchaseType) {
            updateUIforPurcahseService();
        } else {
            binder.receivedAmountEt.requestFocus();
        }
        //updating the visibility of camera icon
        binder.ivTakeImage.setVisibility(isProofRequired() || isAtmType ? View.VISIBLE : View.GONE);
    }

    */
/**
     * check whether contains cod booking
     *
     * @return contains or not
     *//*

    private boolean containsCodBooking() {
        if (Utils.isNewBatchService(batchServiceCode)) {
            for (BatchBooking batchBooking : callData.getBookingList())
                if (batchBooking.getServiceCode() == Constants.ServiceCode.SEND_COD)
                    return true;
        }
        return false;
    }

    */
/**
     * this will update the total amount for invoice
     *
     * @param invoiceList list of fields in invoice
     * @return total value
     *//*

    private String updateTotal(ArrayList<Invoice> invoiceList) {
        String total = StringUtils.EMPTY;
        for (Invoice invoice : invoiceList) {
            if (invoice.getField() != null && invoice.getField().equalsIgnoreCase("total")) {
                total = invoice.getValue();
                break;
            }
        }
        if (StringUtils.isNotEmpty(total)) {
            totalCharges = total;
            callData.setTotalFare(totalCharges);
            if (callData.getServiceCode() == MART_LESS_THAN_TWO) {
                binder.receivedAmountEt.setHint("Suggested Rs. " + Utils.getCommaFormattedAmount(totalCharges));
            }
        }
        return total;
    }

    */
/**
     * this will initialize the call data object
     *//*

    private void initCallData() {
        callData = AppPreferences.getCallData();
        isNewBatchFlow = Utils.isNewBatchService(callData.getServiceCode());
        //this will manage the single ride flow
        if (!isNewBatchFlow) return;
        //extracting batch service code
        batchServiceCode = callData.getServiceCode();
        batchId = callData.getTripId();
        //CHECK FOR SINGLE BOOKING AND SET IS_PAID VALUE ACCORDINGLY
        if (callData.getExtraParams() != null) {
            isPaid = callData.getExtraParams().isPaid();
        }
        // check for finished trip
        ArrayList<BatchBooking> bookingResponseList = callData.getBookingList();

        //this will be the finished trip

        for (BatchBooking tripData : bookingResponseList) {
            // if trip status if "finished", getting trip details
            if (tripData.getStatus().
                    equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                trip = tripData;
                break;
            }
        }

        if (StringUtils.isNotEmpty(trip.getDeliveryMessage())) {
            try {
                Integer position = Integer.valueOf(trip.getDeliveryMessage());
                AppPreferences.setLastSelectedMsgPosition(Integer.parseInt(trip.getDeliveryMessage()), null);
                selectedMsgPosition = position;
                returnRunOnBooking = trip.isReturnRun();
                if (returnRunOnBooking) {
                    disableSpinner();
                }
            } catch (Exception e) {

            }
        }
        callData.setCallType(batchServiceCode == NEW_BATCH_DELIVERY_COD ? Constants.CallType.COD : Constants.CallType.NOD);
        callData.setTotalFare("0"); //this will automatically update through the invoice api
        callData.setTripId(trip.getId());
        callData.setTripNo(trip.getBookingCode());
        callData.setServiceCode(trip.getServiceCode());
        if (trip.getDropoff() != null) {
            callData.setEndAddress(trip.getDropoff().getGpsAddress());
            callData.setEndLat(String.valueOf(trip.getDropoff().getLat()));
            callData.setEndLng(String.valueOf(trip.getDropoff().getLng()));
        }
        if (trip.getPickup() != null) {
            callData.setStartAddress(trip.getPickup().getGpsAddress());
            callData.setStartLat(String.valueOf(trip.getPickup().getLat()));
            callData.setStartLng(String.valueOf(trip.getPickup().getLng()));
        }
        mLastReturnRunBooking = trip.getDisplayTag().equalsIgnoreCase("z");

        //checking for reroute
        if (StringUtils.isNotEmpty(trip.getDropoff().getRerouteBookingId())) {
            //trip contains reroute
            for (BatchBooking tripData : bookingResponseList) {
                //find for routed booking
                if (trip.getDropoff().getRerouteBookingId().equalsIgnoreCase(tripData.getId())) {
                    //delivery data is mapping with failed data
                    DeliveryDetails deliveryDetails = new DeliveryDetails();
                    deliveryDetails.setDetails(new DeliveryDetailInfo());
                    deliveryDetails.setDropoff(new DeliveryDetailsLocationInfoData());
                    deliveryDetails.getDropoff().setZone_dropoff_name_urdu(tripData.getDropoff().getAddress());
                    deliveryDetails.getDetails().setTrip_id(tripData.getId());
                    reRouteDeliveryDetails = deliveryDetails;
                    selectedMsgPosition = AppPreferences.getLastSelectedMsgPosition();
                    break;
                }
            }
        }
    }

    private void updateUIforPurcahseService() {
        binder.receivedAmountEt.clearFocus();
        binder.llKharedari.setVisibility(View.VISIBLE);
        binder.kharedariAmountEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        binder.kharedariAmountEt.requestFocus();
        initKhareedadiSuggestion();
        binder.kharedariAmountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (StringUtils.isNotBlank(binder.kharedariAmountEt.getText().toString())
                        && !binder.kharedariAmountEt.getText().toString().equalsIgnoreCase(lastKhareedariAmount)) {
                    lastKhareedariAmount = binder.kharedariAmountEt.getText().toString();
                    totalCharges = "" + (Integer.parseInt(lastKhareedariAmount) + Integer.parseInt(callData.getTotalFare()));
                    binder.receivedAmountEt.setHint("Suggested Rs. " + Utils.getCommaFormattedAmount(totalCharges));
                } else if (StringUtils.isBlank(binder.kharedariAmountEt.getText().toString())) {
                    initKhareedadiSuggestion();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initKhareedadiSuggestion() {
        lastKhareedariAmount = StringUtils.EMPTY;
        totalCharges = callData.getTotalFare();
        binder.receivedAmountEt.setHint("Suggested Rs. " + Utils.getCommaFormattedAmount(totalCharges));
    }

    private void updateUIICODelivery() {
        binder.llReceiverInfo.setVisibility(View.VISIBLE);
        binder.llReceiverInfo.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen._8sdp));

        binder.rlDeliveryStatus.setVisibility(View.VISIBLE);
        binder.ivRight0.setImageDrawable(Utils.changeDrawableColor(this, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        binder.receivedAmountEt.clearFocus();
        binder.etReceiverName.requestFocus();
    }

    private void updateUIBykeaCash() {
        binder.endAddressTv.setVisibility(View.GONE);
        binder.dottedLine.setVisibility(View.GONE);
        binder.icPin.setVisibility(View.GONE);
        binder.addressDivider.setVisibility(View.GONE);

        binder.rlDeliveryStatus.setVisibility(View.VISIBLE);
        binder.ivRight0.setImageDrawable(Utils.changeDrawableColor(this, R.drawable.polygon, R.color.blue_dark));
        initAdapter(callData);

        binder.receivedAmountEt.requestFocus();
    }

    private void updateUIForAtmBooking() {

        binder.endAddressTv.setVisibility(View.GONE);
        binder.dottedLine.setVisibility(View.GONE);
        binder.icPin.setVisibility(View.GONE);
        binder.addressDivider.setVisibility(View.GONE);
        binder.rlDeliveryStatus.setVisibility(View.GONE);

        binder.llReceiverInfo.setVisibility(View.VISIBLE);
        binder.receiverNameContainer.setVisibility(View.GONE);
        binder.cnicNumberContainer.setVisibility(View.VISIBLE);
        initAdapter(callData);

        binder.receivedAmountEt.requestFocus();
    }

    private String[] getMessageList() {
        if (isBykeaCashType) return Utils.getBykeaCashJobStatusMsgList(this);
        else if (mLastReturnRunBooking) return new String[]{getString(R.string.return_run_spinner)};
        else return Utils.getDeliveryMsgsList(this);
    }

    private void initAdapter(final NormalCallData callData) {

        final String[] list = getMessageList();

        final DeliveryMsgsSpinnerAdapter adapter = new DeliveryMsgsSpinnerAdapter(this, list);

        binder.spDeliveryStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {

                if (view != null) {
                    view.findViewById(R.id.tvItem).setPadding(0, 0, (int) getResources().getDimension(R.dimen._34sdp), 0);
                } else {
                    final ViewTreeObserver layoutObserver = binder.spDeliveryStatus.getViewTreeObserver();
                    layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            View selectedView = binder.spDeliveryStatus.getSelectedView();
                            if (selectedView != null) {
                                selectedView.findViewById(R.id.tvItem).setPadding(0, 0, (int) getResources().getDimension(R.dimen._34sdp), 0);
                            }
                            binder.spDeliveryStatus.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }

                if (reRouteDeliveryDetails != null && AppPreferences.getLastSelectedMsgPosition() != position) {
                    binder.spDeliveryStatus.setSelection(AppPreferences.getLastSelectedMsgPosition());
                    return;
                }
                AppPreferences.setLastSelectedMsgPosition(position, list[position]);
                selectedMsgPosition = position;
                updateAdapter();
                handleInputInfoForBatch(selectedMsgPosition == NumberUtils.INTEGER_ZERO);
                if (StringUtils.isNotBlank(callData.getCodAmount()) && (callData.isCod() || isBykeaCashType)) {
                    if (position == 0) {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit() + Integer.parseInt(callData.getCodAmountNotFormatted());
                        totalCharges = "" + (Integer.parseInt(callData.getTotalFare()) + Integer.parseInt(callData.getCodAmountNotFormatted()));
                        isJobSuccessful = true;
                    } else {
//                        PARTNER_TOP_UP_NEGATIVE_LIMIT = AppPreferences.getSettings().getSettings().getTop_up_limit();
                        totalCharges = callData.getTotalFare();
                        isJobSuccessful = false;
                    }
                }
                if (isProofRequired() && selectedMsgPosition == NumberUtils.INTEGER_ZERO) {
                    binder.ivTakeImage.setVisibility(View.VISIBLE);
                } else {
                    binder.ivTakeImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binder.spDeliveryStatus.setAdapter(adapter);
        binder.spDeliveryStatus.setSelection(selectedMsgPosition);

        //waiting to complete the execution of @{onItemSelected}
        if (returnRunOnBooking) {
            new Handler().postDelayed(() -> {
                disableSpinner();
                callData.setReturnRun(true);
                updateFailureDeliveryLabel(null);
                binder.feedbackBtn.setEnabled(true);
            }, 1500);
        }
    }

    private long mLastClickTime;

   // @OnClick({R.id.ivTakeImage, R.id.ivEyeView, R.id.feedbackBtn, R.id.ivBatchInfo, R.id.ivPickUpCustomerPhone, R.id.imageViewAddDelivery})
    @Override
    public void onClick(View v) {
        if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()) {
            case R.id.ivTakeImage:
                prepareForTakingPictureFromCamera();
                break;
            case R.id.ivEyeView:
                previewImage();
                break;
            case R.id.imageViewAddDelivery:
                String tripId = callData.getTripId();
                if (reRouteDeliveryDetails != null) {
                    tripId = reRouteDeliveryDetails.getDetails().getTrip_id();
                }
                new BatchNaKamiyabDialog(batchId, tripId, new BatchNaKamiyabDialog.OnResult() {
                    @Override
                    public void onReturnRun() {
                        callData.setReturnRun(true);
                        updateFailureDeliveryLabel(null);
                        binder.feedbackBtn.setEnabled(true);
                        disableSpinner();
                    }

                    @Override
                    public void onReRoute() {
                    }

                }).show(getSupportFragmentManager());
                break;
            case R.id.ivPickUpCustomerPhone:
                String phoneNumber = callData.getSenderPhone();
                if (StringUtils.isNotBlank(phoneNumber)) {
                    if (Utils.isAppInstalledWithPackageName(this, Constants.ApplicationsPackageName.WHATSAPP_PACKAGE) ||
                            Utils.isAppInstalledWithPackageName(this, Constants.ApplicationsPackageName.WHATSAPP_BUSINESS_PACKAGE)) {
                        Utils.openCallDialog(this, callData, phoneNumber);
                    } else {
                        Utils.callingIntent(this, phoneNumber);
                    }
                }
                break;
            case R.id.ivBatchInfo:
                if (batchInvoiceList != null)
                    Dialogs.INSTANCE.showReturnRunInvoice(FSImplFeedbackActivity.this, batchInvoiceList, null);
                break;

            case R.id.feedbackBtn:
                if (valid()) {
                    Dialogs.INSTANCE.showLoader(this);
                    logMPEvent();
                    if (isProofRequired() || isAtmType) {
                        optimizeAndUpload();
                    } else {
                        finishTrip();
                    }
                }
                break;
        }
    }

    private void optimizeAndUpload() {
        //   new AsynctaskOptimizeImages(photoImageFile.toString(), path -> uploadProofOfDelivery()).execute();
        uploadProofOfDelivery();
    }

    private void finishTrip() {

        JobsDataSource.ConcludeJobCallback jobCallback = new JobsDataSource.ConcludeJobCallback() {

            @Override
            public void onJobConcluded(@NotNull ConcludeJobBadResponse response) {
                Dialogs.INSTANCE.dismissDialog();
                Dialogs.INSTANCE.showToast(response.getMessage());
                //handled old flow if not a batch service
                if (allBookingInCompletedState()) {
                    Utils.setCallIncomingState();
                    ActivityStackManager.getInstance().startHomeActivity(true, FSImplFeedbackActivity.this);
                    finish();
                } else {
                    //check if contains any pending booking or has any failed booking
                    finish();
                }
            }


            @Override
            public void onJobConcludeFailed(@Nullable String message, @Nullable Integer code) {
                Dialogs.INSTANCE.dismissDialog();
                if (code != null && code == HTTPStatus.UNAUTHORIZED) {
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                } else {
                    Dialogs.INSTANCE.showError(FSImplFeedbackActivity.this, binder.feedbackBtn, message);
                }
            }
        };

        boolean isLoadboardJob = Utils.isModernService(callData.getServiceCode());
        if (isBykeaCashType) {
            if (isLoadboardJob) {
                String name = callData.getSenderName() != null ? callData.getSenderName() : callData.getPassName();
                String number = callData.getSenderPhone() != null ? callData.getSenderPhone() : callData.getPhoneNo();

                repo.concludeJob(
                        callData.getTripId(),
                        (int) binder.callerRb.getRating(),
                        Integer.parseInt(binder.receivedAmountEt.getText().toString()),
                        jobCallback,
                        Utils.getBykeaCashJobStatusMsgList(this)[selectedMsgPosition],
                        selectedMsgPosition == 0,
                        null,
                        name,
                        number,
                        null,
                        AppPreferences.getADID()
                );
            } else
                new UserRepository().requestFeedback(
                        this,
                        handler,
                        StringUtils.EMPTY,
                        binder.callerRb.getRating() + StringUtils.EMPTY,
                        binder.receivedAmountEt.getText().toString(),
                        selectedMsgPosition == NumberUtils.INTEGER_ZERO,
                        Utils.getBykeaCashJobStatusMsgList(this)[selectedMsgPosition],
                        binder.etReceiverName.getText().toString(),
                        binder.etReceiverMobileNo.getText().toString()
                );
        } else if (isDeliveryType || isOfflineDeliveryType) {
            if (isLoadboardJob)
                repo.concludeJob(
                        callData.getTripId(),
                        (int) binder.callerRb.getRating(),
                        Integer.valueOf(binder.receivedAmountEt.getText().toString()),
                        jobCallback,
                        getDeliveryFeedback(),
                        selectedMsgPosition == NumberUtils.INTEGER_ZERO,
                        null,
                        binder.etReceiverName.getText().toString(),
                        binder.etReceiverMobileNo.getText().toString(),
                        null,
                        AppPreferences.getADID()
                );
            else
                new UserRepository().requestFeedback(this, handler,
                        "Nice driver", binder.callerRb.getRating() + StringUtils.EMPTY, binder.receivedAmountEt.getText().toString()
                        , selectedMsgPosition == NumberUtils.INTEGER_ZERO, getDeliveryFeedback(), binder.etReceiverName.getText().toString(),
                        binder.etReceiverMobileNo.getText().toString());
        } else if (isPurchaseType) {
            if (isLoadboardJob)
                repo.concludeJob(callData.getTripId(), (int) binder.callerRb.getRating(), Integer.valueOf(binder.receivedAmountEt.getText().toString()),
                        jobCallback, null, null,
                        Integer.valueOf(binder.kharedariAmountEt.getText().toString()), null, null, null,
                        AppPreferences.getADID());
            else
                new UserRepository().requestFeedback(this, handler,
                        "Nice driver", binder.callerRb.getRating() + StringUtils.EMPTY, binder.receivedAmountEt.getText().toString(),
                        binder.kharedariAmountEt.getText().toString());
        } else if (isAtmType) {
            repo.concludeJob(
                    callData.getTripId(),
                    (int) binder.callerRb.getRating(),
                    Integer.parseInt(binder.receivedAmountEt.getText().toString()),
                    jobCallback,
                    null,
                    true,
                    null,
                    null,
                    binder.etReceiverMobileNo.getText().toString(),
                    binder.etCnicNumber.getText().toString(),
                    AppPreferences.getADID());
        } else {
            if (isLoadboardJob)
                repo.concludeJob(
                        callData.getTripId(),
                        (int) binder.callerRb.getRating(),
                        Integer.parseInt(binder.receivedAmountEt.getText().toString()),
                        jobCallback,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AppPreferences.getADID());
            else
                new UserRepository().requestFeedback(this, handler,
                        "Nice driver", binder.callerRb.getRating() + StringUtils.EMPTY, binder.receivedAmountEt.getText().toString());
        }


    }

    private void uploadProofOfDelivery() {
        if (AppPreferences.getDriverSettings() != null &&
                AppPreferences.getDriverSettings().getData() != null &&
                StringUtils.isNotBlank(AppPreferences.getDriverSettings().getData().getS3BucketPod())) {
            BykeaAmazonClient.INSTANCE.uploadFile(photoImageFile.getName(), photoImageFile, new com.bykea.pk.partner.utils.audio.Callback<String>() {
                @Override
                public void success(String obj) {
                    repo.pushTripDetails(callData.getTripId(), obj, new JobsDataSource.PushTripDetailCallback() {
                        @Override
                        public void onSuccess() {
                            finishTrip();
                        }

                        @Override
                        public void onFail(int code, @Nullable String message) {
                            Dialogs.INSTANCE.dismissDialog();
                            Dialogs.INSTANCE.showToast(message);
                        }
                    });
                }

                @Override
                public void fail(int errorCode, @NotNull String errorMsg) {
                    finishTrip();
                }
            }, AppPreferences.getDriverSettings().getData().getS3BucketPod());
        } else {
            finishTrip();
        }
    }

    private String getDeliveryFeedback() {
        if (mLastReturnRunBooking) {
            return getString(R.string.return_run_spinner);
        }
        return Utils.getDeliveryMsgsList(this)[selectedMsgPosition];
    }

    private boolean allBookingInCompletedState() {
        if (isNewBatchFlow) {
            //saving foreach, if booking is not a return run
            if (!callData.isReturnRun()) {
                for (BatchBooking batchBooking : callData.getBookingList()) {
                    if (!batchBooking.isCompleted()) {
                        return true;
                    }
                }
            }
            //need to handle re-route case above return run
            if (isRerouteCreated) {
                return false;
            }
            //checking whether z's booking exist
            if (callData.isReturnRun()) {
                return Utils.containsReturnRunBooking(callData.getBookingList());
            }
            return false;
        } else
            return true;
    }

    private void logMPEvent() {
        try {
            JSONObject properties = new JSONObject();
            properties.put("TripID", callData.getTripId());
            properties.put("TripNo", callData.getTripNo());
            properties.put("PassengerID", callData.getPassId());
            properties.put("DriverID", AppPreferences.getPilotData().getId());
            properties.put("Amount", callData.getTrip_charges());
            properties.put("AmountEntered", binder.receivedAmountEt.getText().toString());
            properties.put("Time", callData.getTotalMins() + "");
            properties.put("KM", callData.getDistanceCovered());
            properties.put("type", callData.getCallType());
            properties.put("timestamp", Utils.getIsoDate());
            properties.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

            properties.put("PassengerName", callData.getPassName());
            properties.put("DriverName", AppPreferences.getPilotData().getFullName());
            if (StringUtils.isNotBlank(callData.getPromo_deduction())) {
                properties.put("PromoDeduction", callData.getPromo_deduction());
            } else {
                properties.put("PromoDeduction", "0");
            }
            if (StringUtils.isNotBlank(callData.getWallet_deduction())) {
                properties.put("WalletDeduction", callData.getWallet_deduction());
            } else {
                properties.put("WalletDeduction", "0");
            }
            Utils.logEvent(this, callData.getPassId(), Constants.AnalyticsEvents.RIDE_FARE.replace(
                    Constants.AnalyticsEvents.REPLACE, callData.getCallType()), properties);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onFeedback(final FeedbackResponse feedbackResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(feedbackResponse.getMessage());
                    Utils.setCallIncomingState();
                    AppPreferences.setWalletAmountIncreased(!feedbackResponse.isAvailable());
                    AppPreferences.setAvailableStatus(feedbackResponse.isAvailable());
                    ActivityStackManager.getInstance().startHomeActivity(true, FSImplFeedbackActivity.this);
                    finish();
                }
            });

        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    if (errorCode == HTTPStatus.UNAUTHORIZED) {
                        EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    } else {
                        Dialogs.INSTANCE.showError(FSImplFeedbackActivity.this, binder.feedbackBtn, errorMessage);
                    }
                }
            });
        }
    };

    */
/**
     * Feedback validation on the following cases.
     *
     * <ul>
     * <li>Check that the amount lie in the digit only regix</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the entered amount should not be greater than
     * {@link FSImplFeedbackActivity#AMOUNT_LIMIT}</li>
     * <li>Check that the entered amount should be same to the total charges</li>
     * <li>Check that the rating should be given</li>
     * <li>Check that the amount should be entered & should not less than 0</li>
     * </ul>
     *
     * @return true if all the validation is true otherwise false
     *//*

    private boolean valid() {
        if (isNewBatchFlow && selectedMsgPosition != NumberUtils.INTEGER_ZERO) {
            binder.receivedAmountEt.setText(String.valueOf(NumberUtils.INTEGER_ZERO));
            return true;
        }
        if (isPurchaseType && StringUtils.isBlank(binder.kharedariAmountEt.getText().toString())) {
            binder.kharedariAmountEt.setError(getString(R.string.enter_amount));
            binder.kharedariAmountEt.requestFocus();
            return false;
        } else if (StringUtils.isBlank(binder.receivedAmountEt.getText().toString())) {
            setEtError(getString(R.string.enter_received_amount));
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType) && selectedMsgPosition == Constants.KAMYAB_DELIVERY && StringUtils.isBlank(binder.etReceiverName.getText().toString())) {
            binder.etReceiverName.setError(getString(R.string.error_field_empty));
            binder.etReceiverName.requestFocus();
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType) && selectedMsgPosition == Constants.KAMYAB_DELIVERY && StringUtils.isBlank(binder.etReceiverMobileNo.getText().toString())) {
            binder.etReceiverMobileNo.setError(getString(R.string.error_field_empty));
            binder.etReceiverMobileNo.requestFocus();
            return false;
        } else if ((isDeliveryType || isOfflineDeliveryType || isPurchaseType) && StringUtils.isNotBlank(binder.etReceiverMobileNo.getText().toString())
                && !Utils.isValidNumber(this, binder.etReceiverMobileNo)) {
            return false;
        } else if (!binder.receivedAmountEt.getText().toString().matches(Constants.REG_EX_DIGIT)) {
            setEtError(getString(R.string.error_invalid_amount));
            return false;
        } else if (!isPaid && totalCharges.matches(Constants.REG_EX_DIGIT)
                && Integer.parseInt(binder.receivedAmountEt.getText().toString()) < Integer.parseInt(totalCharges)
                && (!isBykeaCashType || isJobSuccessful)) {
            setEtError(getString(R.string.error_amount_greater_than_total));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                driverWallet <= PARTNER_TOP_UP_NEGATIVE_LIMIT &&
                Integer.parseInt(binder.receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT + Constants.DIGIT_ONE) &&
                !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
            //WHEN THE WALLET IS LESS THAN ZERO, RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP NEGATIVE LIMIT)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_NEGATIVE_LIMIT)));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                (driverWallet > PARTNER_TOP_UP_NEGATIVE_LIMIT && driverWallet < PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                Integer.parseInt(binder.receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + driverWallet + Constants.DIGIT_ONE) &&
                !Util.INSTANCE.isBykeaCashJob(callData.getServiceCode())) {
            //WHEN THE WALLET IS GREATER THAN ZERO BUT LESS THAN THE MAX POSITIVE TOP UP LIMIT,
            //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND WALLET)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + driverWallet)));
            return false;
        } else if (totalCharges.matches(Constants.REG_EX_DIGIT) &&
                (Util.INSTANCE.isBykeaCashJob(callData.getServiceCode()) || driverWallet >= PARTNER_TOP_UP_POSITIVE_LIMIT) &&
                Integer.parseInt(binder.receivedAmountEt.getText().toString()) >= (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT + Constants.DIGIT_ONE)) {
            //WHEN THE WALLET IS GREATER THAN MAX POSITIVE TOP UP LIMIT,
            //RECEIVED AMOUNT CAN NOT BE GREATER THAN THE SUM OF (TOTAL CHARGES AND PARTNER TOP UP POSITIVE LIMIT)
            setEtError(getString(R.string.amount_error, (Integer.parseInt(totalCharges) + PARTNER_TOP_UP_POSITIVE_LIMIT)));
            return false;
        } else if (Integer.parseInt(binder.receivedAmountEt.getText().toString()) >= (AMOUNT_LIMIT + Constants.DIGIT_ONE)) {
            setEtError(getString(R.string.amount_error, AMOUNT_LIMIT));
            return false;
        } else if (binder.callerRb.getRating() <= 0.0) {
            Dialogs.INSTANCE.showError(this, binder.feedbackBtn, getString(R.string.passenger_rating));
            return false;
        } else if ((isProofRequired() || isAtmType) && photoImageFile == null) {
            if (callData != null && callData.getServiceCode() != null && callData.getServiceCode() == MART_LESS_THAN_TWO) {
                Dialogs.INSTANCE.showAlertDialogTick(this, null, getString(R.string.valid_purchase_receipt_image_required), view -> Dialogs.INSTANCE.dismissDialog());
            } else {
                Dialogs.INSTANCE.showAlertDialogTick(this, null, getString(R.string.valid_image_required), view -> Dialogs.INSTANCE.dismissDialog());
            }
            return false;
        } else if (StringUtils.isNotBlank(binder.receivedAmountEt.getText().toString())) {
            try {
                int receivedPrice = Integer.parseInt(binder.receivedAmountEt.getText().toString());
                if (receivedPrice < 0) {
                    setEtError(getString(R.string.amount_not_acceptable));
                    return false;
                }
            } catch (Exception e) {
                setEtError(getString(R.string.amount_not_acceptable));
                return false;
            }
        }
        return true;
    }

    private boolean isProofRequired() {
        List<String> codes = AppPreferences.getSettings().getSettings().getPodServiceCodes();
        boolean isRequired = false;
        for (String code : codes) {
            if (callData.getServiceCode() != null && code.equalsIgnoreCase(String.valueOf(callData.getServiceCode()))) {
                isRequired = true;
                break;
            }
        }
        return isRequired && selectedMsgPosition == NumberUtils.INTEGER_ZERO;
    }

    private void setEtError(String error) {
        binder.receivedAmountEt.setError(error);
        binder.receivedAmountEt.requestFocus();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Permissions.LOCATION_PERMISSION) {
            LocationManager locationManager;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Dialogs.INSTANCE.showLocationSettings(this, Permissions.LOCATION_PERMISSION);
            else {
                ActivityStackManager.getInstance().startLocationService(this);
            }
        } else if (requestCode == RC_ADD_EDIT_DELIVERY_DETAILS) {
            if (resultCode == RESULT_OK) {
                onRerouteCreated(data.getParcelableExtra(DELIVERY_DETAILS_OBJECT));
            }
        }
    }

    private void previewImage() {
        binder.ivEyeView.setVisibility(View.VISIBLE);
        binder.ivTakeImage.setVisibility(View.GONE);
        Dialogs.INSTANCE.showChangeImageDialog(FSImplFeedbackActivity.this, photoBitmap, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }, view -> {
            Dialogs.INSTANCE.dismissDialog();
            takePicture();
        });
    }

    private void createFileAndUriForPhoto() {
        try {
            photoImageFile = Utils.createImageFile(FSImplFeedbackActivity.this, Constants.POD);
            photoImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", photoImageFile);
        } catch (IOException e) {
            Dialogs.INSTANCE.showToast(e.getLocalizedMessage());
        }
    }

    private void prepareForTakingPictureFromCamera() {
        if (photoImageFile == null || photoImageUri == null) {
            createFileAndUriForPhoto();
        } else {
            if (hasCameraPermissions()) {
                takePicture();
            } else {
                requestSinglePermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        }
    }

    private boolean hasCameraPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private final ActivityResultLauncher<String> requestSinglePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        if (granted) {
            takePicture();
        } else {
            Toast.makeText(FSImplFeedbackActivity.this, getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
        }
    });

    private final ActivityResultLauncher<Uri> getCameraImageLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
        if (success) {
            photoBitmap = BitmapExtKt.getRotatedBitmap(photoImageUri, getContentResolver());
            previewImage();
        }
    });

    private void takePicture() {
        getCameraImageLauncher.launch(photoImageUri);
    }

    private void onRerouteCreated(DeliveryDetails data) {
        isRerouteCreated = true;
        binder.feedbackBtn.setEnabled(true);
        disableSpinner();
        reRouteDeliveryDetails = data;
        updateFailureDeliveryLabel(data);
    }

    private void disableSpinner() {
        binder.spDeliveryStatus.setEnabled(false);
        binder.spDeliveryStatus.setClickable(false);
    }

    private void updateFailureDeliveryLabel(DeliveryDetails deliveryDetails) {
        if (!isNewBatchFlow) return;
        String formattedString = getResources().getString(R.string.problem_item);
        if (deliveryDetails != null) {
            binder.llFailureDelivery.setVisibility(View.VISIBLE);
            binder.imageViewAddDelivery.setVisibility(View.GONE);
            binder.gotoPurchaser.setText(String.format(formattedString, deliveryDetails.getDropoff().getZone_dropoff_name_urdu()));
        } else if (callData.isReturnRun()) {
            binder.llFailureDelivery.setVisibility(View.VISIBLE);
            binder.imageViewAddDelivery.setVisibility(View.GONE);
            binder.gotoPurchaser.setText(String.format(formattedString, getString(R.string.goto_purchaser)));
        }
    }

    */
/**
     * Event Received from Socket (BOOKING_UPDATED)
     *
     * @param response updated data
     *//*

    @Subscribe
    public void onEvent(BookingUpdated response) {
        if (callData != null) {
            if (callData.getExtraParams() != null) {
                callData.getExtraParams().setPaid(response.isPaid());
                isPaid = response.isPaid();
            } else if (trip != null && callData.getBookingList() != null && callData.getBookingList().size() > DIGIT_ZERO) {
                for (BatchBooking tripData : callData.getBookingList()) {
                    if (trip.getId().equalsIgnoreCase(tripData.getId()) && tripData.getStatus().equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                        //CHECK FOR RESPECTIVE BOOKING FROM BATCH AND SET IS_PAID VALUE ACCORDINGLY
                        if (tripData.getExtraParams() != null) {
                            tripData.getExtraParams().setPaid(response.isPaid());
                            isPaid = response.isPaid();
                        }
                        break;
                    }
                }
            }
        }
    }
}*/
