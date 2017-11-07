package com.zyl.mp3cutter.home.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zyl.mp3cutter.R;
import com.zyl.mp3cutter.home.bean.MusicInfo;

import java.util.ArrayList;


public class FileChooserAdapter extends BaseAdapter {

	private ArrayList<MusicInfo> data = new ArrayList<MusicInfo>();
	private LayoutInflater mLayoutInflater = null;

	private static ArrayList<String> MUSIC_SUFFIX = new ArrayList<String>();

	static {
		MUSIC_SUFFIX.add(".mp3");
		// PPT_SUFFIX.add(".pptx");
	}

	public FileChooserAdapter(Context context) {
		super();
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<MusicInfo> data) {
		this.data.clear();
		this.data.addAll(data);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public MusicInfo getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mLayoutInflater.inflate(R.layout.adapter_filechooser_gridview_item,
					null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		MusicInfo fileInfo = getItem(position);
		holder.tvFileName.setText(fileInfo.getFilename());

//		if (fileInfo.isDirectory()) {
//			// holder.imgFileIcon.setImageResource(R.drawable.ic_folder);
//			holder.tvFileName.setTextColor(Color.GRAY);
//		} else if (fileInfo.isMUSICFile()) {
//			holder.imgFileIcon.setImageResource(R.drawable.ic_ppt);
			holder.tvFileName.setTextColor(Color.GRAY);
//		}
		// else {
		// holder.imgFileIcon.setImageResource(R.drawable.ic_file_unknown);
		// holder.tvFileName.setTextColor(Color.GRAY);
		// }
		return view;
	}

	static class ViewHolder {
//		ImageView imgFileIcon;
		TextView tvFileName;

		public ViewHolder(View view) {
//			imgFileIcon = (ImageView) view.findViewById(R.id.imgFileIcon);
			tvFileName = (TextView) view.findViewById(R.id.tvFileName);
		}
	}

	enum FileType {
		FILE, DIRECTORY;
	}

	// =========================
	// Model
	// =========================
	public static class FileInfo {
		private FileType fileType;
		private String fileName;
		private String filePath;

		public FileInfo(String filePath, String fileName, boolean isDirectory) {
			this.filePath = filePath;
			this.fileName = fileName;
			fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
		}

		public boolean isMUSICFile() {
			if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
				return false;
			String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
			if (!isDirectory() && MUSIC_SUFFIX.contains(fileSuffix))
				return true;
			else
				return false;
		}

		public boolean isDirectory() {
			if (fileType == FileType.DIRECTORY)
				return true;
			else
				return false;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		@Override
		public String toString() {
			return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
					+ ", filePath=" + filePath + "]";
		}
	}

}
