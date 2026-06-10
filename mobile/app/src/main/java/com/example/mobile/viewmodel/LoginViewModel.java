package com.example.mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.model.UserCredentials;
import com.example.mobile.repository.LoginRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {
    private final LoginRepository repository;
    private final ExecutorService executorService;

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel() {
        this.repository = LoginRepository.getInstance();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String username, String password, boolean rememberMe) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Simple validation
        if (username == null || username.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập tài khoản");
            loginSuccess.setValue(false);
            isLoading.setValue(false);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập mật khẩu");
            loginSuccess.setValue(false);
            isLoading.setValue(false);
            return;
        }

        // Perform network authentication on background thread
        executorService.execute(() -> {
            UserCredentials credentials = new UserCredentials(username.trim(), password);
            LoginRepository.AuthResult result = repository.authenticate(credentials);
            
            if (result.isSuccess()) {
                loginSuccess.postValue(true);
            } else {
                errorMessage.postValue(result.getMessage());
                loginSuccess.postValue(false);
            }
            isLoading.postValue(false);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
