package assembly.stone.itassembly.animetaste.ui;import java.io.Serializable;import java.util.ArrayList;import java.util.List;import org.apache.commons.lang.StringUtils;import android.annotation.SuppressLint;import android.app.Fragment;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.view.View.OnClickListener;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.PopupWindow.OnDismissListener;import android.widget.ProgressBar;import android.widget.TextView;import assembly.stone.itassembly.R;import assembly.stone.itassembly.animetaste.adapter.AnimentasteAdapter;import assembly.stone.itassembly.animetaste.entity.AnimetasteModel;import assembly.stone.itassembly.animetaste.entity.AnimetasteResult;import assembly.stone.itassembly.animetaste.entity.AnimetasteResult.AnimeInfo;import assembly.stone.itassembly.animetaste.entity.AnimetasteResult.Animetaste;import assembly.stone.itassembly.animetaste.entity.ApiConnector;import assembly.stone.itassembly.animetaste.entity.OnAnimetPopuWinItemClick;import assembly.stone.itassembly.base.intf.OnItemLoadListenter;import assembly.stone.itassembly.base.ui.BaseHomeFragment;import assembly.stone.itassembly.util.ConstantUtils;import assembly.stone.itassembly.util.JsonBinder;import assembly.stone.itassembly.wedget.BaseListView;import assembly.stone.itassembly.wedget.swiptlistview.BaseSwipeRefreshLayout;/** * A simple {@link Fragment} subclass. Activities that contain this fragment * must implement the {@link AnimetasteFragment.OnFragmentInteractionListener} * interface to handle interaction events. Use the * {@link AnimetasteFragment#newInstance} factory method to create an instance * of this fragment. */@SuppressLint("NewApi")public class AnimetasteFragment extends BaseHomeFragment implements OnItemLoadListenter, OnAnimetPopuWinItemClick {	private List<AnimetasteModel> list = new ArrayList<AnimetasteModel>();	private AnimentasteAdapter adapter;	private LinearLayout type_btn;	private AnimetPopuWin popuWin;	private TextView anime_tx;	private int category = 1;	private ImageView anime_img;	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setOnItemLoadListenter(this);		popuWin = new AnimetPopuWin(mActivity);		popuWin.setOnAnimetPopuWinItemClick(this);		layoutResourceId = R.layout.fragment_animetaste;		tag = "httpForAnimetaste";	}	@Override	protected void initView() {		swipeLayout = (BaseSwipeRefreshLayout) mHoseView.findViewById(R.id.aswipe_container);		mListView = (BaseListView) mHoseView.findViewById(R.id.alistview);		type_btn = (LinearLayout) mHoseView.findViewById(R.id.type_btn);		anime_img = (ImageView) mHoseView.findViewById(R.id.anime_img);		anime_tx = (TextView) mHoseView.findViewById(R.id.anime_tx);		loading_progress = (ProgressBar) mHoseView.findViewById(R.id.loading_progress);		adapter = new AnimentasteAdapter(mActivity, list);		mListView.setAdapter(adapter);		page = 1;		super.initView();		type_btn.setOnClickListener(new OnClickListener() {			@Override			public void onClick(View view) {				anime_img.setBackgroundResource(R.drawable.anime_type_t);				popuWin.showAsDropDown(mHoseView.findViewById(R.id.anime_img));			}		});		popuWin.setOnDismissListener(new OnDismissListener() {			@Override			public void onDismiss() {				anime_img.setBackgroundResource(R.drawable.anime_type);			}		});	}	@Override	protected void fillData(String response) {		AnimetasteResult request = JsonBinder.fromJson(response, AnimetasteResult.class);		loading_progress.setVisibility(View.GONE);		swipeLayout.setRefreshing(false);		swipeLayout.setLoading(false);		Animetaste animetaste = request.getData();		if (animetaste == null) {			return;		}		if (animetaste.getResult()) {			AnimeInfo animeInfo = animetaste.getList();			if (animeInfo == null) {				return;			}			List<AnimetasteModel> animeList = animeInfo.getAnime();			if (animeList == null) {				return;			} else if (animeList.size() == 0) {				return;			} else if (animeList.get(0) == null) {				return;			}			if (page == 1) {				adapter.setData(animeList);			} else {				for (AnimetasteModel model : animeList) {					adapter.addAnimetasteModel(model);				}			}		} else {		}	}	@Override	public void onRefresh() {		page = 1;		super.onRefresh();	}	@Override	public void onItemClick(int position, long id) {		AnimetasteModel model = adapter.getData((int) id);		if (model != null) {			if (StringUtils.isEmpty(model.getVideoSource().getHd())					|| StringUtils.isEmpty(model.getVideoSource().getSd())					|| StringUtils.isEmpty(model.getVideoSource().getUhd())) {				Intent intent = new Intent();				intent.putExtra(ConstantUtils.CONSTANT_ANIMETASTE_VIDEOURL, model.getVideoUrl());				String title = model.getName();				try {					title = model.getName().split("\\(")[0];				} catch (Exception e) {				}				intent.putExtra(ConstantUtils.CONSTANT_ANIMETASTE_TITLE, title);				intent.setClass(mActivity, AnimetasteWebVideoActivity.class);				startActivity(intent);			} else {				Intent intent = new Intent();				intent.putExtra(ConstantUtils.CONSTANT_ANIMETASTE_MODEL, (Serializable) model);				intent.setClass(mActivity, AnimetasteDetailActivity.class);				startActivity(intent);			}		}	}	@Override	protected String getPageUrl() {		return ApiConnector.getCategory(category, page);	}	@Override	public void onItemClick(String mode, int mode_code) {		anime_tx.setText(mode);		category = mode_code;		onRefresh();		swipeLayout.setRefreshing(true);		mListView.setSelection(0);	}	@Override	public void onLoad() {		try {			page++;			getData();		} catch (Exception e) {		}	}	@Override	protected boolean getListViewData() {		if (adapter == null) {			return false;		}		if (adapter.getCount() > 0) {			return true;		} else {			return false;		}	}}