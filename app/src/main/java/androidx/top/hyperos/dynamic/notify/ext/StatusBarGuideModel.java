package androidx.top.hyperos.dynamic.notify.ext;

public class StatusBarGuideModel {
    private Center center;
    private Left left;
    private Right right;

    public Center getCenter() {
        return this.center;
    }

    public Left getLeft() {
        return this.left;
    }

    public Right getRight() {
        return this.right;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public void setLeft(Left left) {
        this.left = left;
    }

    public void setRight(Right right) {
        this.right = right;
    }

    public String toString() {
        return "StatusBarGuideModel{left=" + this.left + ", right=" + this.right + ", center=" + this.center + '}';
    }
    
    public static class Center {
		private IconParams iconParams;
		private TextParams textParams;

		public IconParams getIconParams() {
			return this.iconParams;
		}

		public TextParams getTextParams() {
			return this.textParams;
		}

		public void setIconParams(IconParams iconParams) {
			this.iconParams = iconParams;
		}

		public void setTextParams(TextParams textParams) {
			this.textParams = textParams;
		}

		public String toString() {
			return "{textParams=" + this.textParams + ", iconParams=" + this.iconParams + '}';
		}
	}
	
	public static class IconParams {
		private String category;
		private String iconFormat;
		private String iconResName;
		private Integer iconType;

		public String getCategory() {
			return this.category;
		}

		public String getIconFormat() {
			return this.iconFormat;
		}

		public String getIconResName() {
			return this.iconResName;
		}

		public Integer getIconType() {
			return this.iconType;
		}

		public void setCategory(String str) {
			this.category = str;
		}

		public void setIconFormat(String str) {
			this.iconFormat = str;
		}

		public void setIconResName(String str) {
			this.iconResName = str;
		}

		public void setIconType(Integer num) {
			this.iconType = num;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("IconParams{iconResName='");
			sb.append(this.iconResName);
			sb.append("', iconType=");
			sb.append(this.iconType);
			sb.append(", iconFormat='");
			sb.append(this.iconFormat);
			sb.append("', category='");
			return Tools.concat(sb, this.category, "'}");
		}
	}
	public static class Left {
		private IconParams iconParams;
		private TextParams textParams;

		public IconParams getIconParams() {
			return this.iconParams;
		}

		public TextParams getTextParams() {
			return this.textParams;
		}

		public void setIconParams(IconParams iconParams) {
			this.iconParams = iconParams;
		}

		public void setTextParams(TextParams textParams) {
			this.textParams = textParams;
		}

		public String toString() {
			return "Left{textParams=" + this.textParams + ", iconParams=" + this.iconParams + '}';
		}
	}
	
	public static class Right {
		private IconParams iconParams;
		private TextParams textParams;

		public IconParams getIconParams() {
			return this.iconParams;
		}

		public TextParams getTextParams() {
			return this.textParams;
		}

		public void setIconParams(IconParams iconParams) {
			this.iconParams = iconParams;
		}

		public void setTextParams(TextParams textParams) {
			this.textParams = textParams;
		}

		public String toString() {
			return "Right{textParams=" + this.textParams + ", iconParams=" + this.iconParams + '}';
		}
	}
	
	public static class TextParams {
		private String text;
		private Integer textColor;

		public String getText() {
			return this.text;
		}

		public Integer getTextColor() {
			return this.textColor;
		}

		public void setText(String str) {
			this.text = str;
		}

		public void setTextColor(Integer num) {
			this.textColor = num;
		}

		public String toString() {
			return "TextParams{text='" + this.text + "', textColor=" + this.textColor + '}';
		}
	}
	
}
