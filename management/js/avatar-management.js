// 删除图片逻辑
const deleteModal = document.getElementById("delete-modal");
const confirmDelete = document.getElementById("confirm-delete");
const cancelDelete = document.getElementById("cancel-delete");
let uploadInput = null;
let currentAvatarItem = null;
let code = null;

// 确认删除
confirmDelete.addEventListener("click", function () {
  if (deleteModal.style.display != "none") {
    deleteModal.style.display = "none"; // 隐藏模态框
    if (currentAvatarItem) {
      jsonRequestWithToken(
        "/user/defaultphoto/remove/" + code,
        "GET",
        null,
        function (data) {
          currentAvatarItem.remove(); // 删除头像
          console.log("图片已删除");
        }
      );
    }
  }
});

// 取消删除
cancelDelete.addEventListener("click", function () {
  deleteModal.style.display = "none"; // 隐藏模态框
});

queryPhotos();

function queryPhotos() {
  jQuery(".avatar-grid").empty();
  jsonRequestWithToken("/user/defaultphoto/list", "GET", null, function (data) {
    for (i = 0; i < data.length; i++) {
      var code = data[i];
      var photo =
        '<div class="avatar-item"><img src="' + getBaseUrl("/" + code);
      photo +=
        '" alt="头像" /><div class="delete-overlay" data-photo-code="' + code;
      photo += '">×</div>';
      jQuery(".avatar-grid").append(jQuery(photo));
    }
    jQuery(".avatar-grid").append(
      jQuery(
        '<div id="upload-button" class="avatar-item" onclick="uploadClick()"><input type="file" id="upload-input" accept="image/*" style="display: none" /><span class="upload-flag">+</span></div>'
      )
    );
    initImageEvent();
  });
}

function initImageEvent() {
  // 上传图片逻辑
  uploadInput = document.getElementById("upload-input");
  uploadInput.addEventListener("change", uploadPhoto);
  // 点击删除按钮时显示模态框
  document.querySelectorAll(".delete-overlay").forEach((overlay) => {
    overlay.addEventListener("click", function (event) {
      event.stopPropagation(); // 阻止事件冒泡
      currentAvatarItem = this.closest(".avatar-item"); // 记录当前点击的头像
      console.log(overlay.getAttribute("data-photo-code"));
      code = overlay.getAttribute("data-photo-code");
      deleteModal.style.display = "flex"; // 显示模态框
    });
  });
}

function uploadClick() {
  uploadInput.click();
}

function uploadPhoto(event) {
  const file = event.target.files[0];
  if (file) {
    if (file.type.startsWith("image/")) {
      const formData = new FormData();
      formData.append("photo", file);
      formRequestWithToken(
        "/user/defaultphoto/upload",
        "POST",
        formData,
        function (data) {
          var photo =
            '<div class="avatar-item"><img src="' + getBaseUrl("/" + data);
          photo +=
            '" alt="头像" /><div class="delete-overlay" data-photo-code="' +
            data;
          photo += '">×</div>';
          jQuery("#upload-button").before(photo);
        }
      );
    } else {
      jQuery.toast({
        heading: "异常",
        text: "请选择有效的图片文件！",
        icon: "warning",
        allowToastClose: true,
      });
    }
  }
}
