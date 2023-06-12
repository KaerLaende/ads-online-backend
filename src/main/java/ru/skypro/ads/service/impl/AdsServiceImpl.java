package ru.skypro.ads.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.ads.dto.AdsDto;
import ru.skypro.ads.dto.CreateAdsDto;
import ru.skypro.ads.dto.FullAdsDto;
import ru.skypro.ads.dto.ResponseWrapperAdsDto;
import ru.skypro.ads.entity.Ads;
import ru.skypro.ads.entity.User;
import ru.skypro.ads.exception.AdsNotFoundException;
import ru.skypro.ads.exception.UserNotFoundException;
import ru.skypro.ads.mapper.AdsMapper;
import ru.skypro.ads.repository.AdsRepository;
import ru.skypro.ads.repository.UserRepository;
import ru.skypro.ads.service.AdsService;

import java.util.List;

@Service
public class AdsServiceImpl implements AdsService {

    private final AdsRepository adsRepository;
    private final UserRepository userRepository;
    private final AdsMapper adsMapper;


    public AdsServiceImpl(AdsRepository adsRepository, UserRepository userRepository, AdsMapper adsMapper) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
        this.adsMapper = adsMapper;
    }

    /**
     * Получает все объявления
     *
     * @return объект {@link ResponseWrapperAdsDto}
     */
    @Override
    public ResponseWrapperAdsDto getAllAds() {
        List<Ads> adsList = adsRepository.findAll();
        System.out.println(adsList);
        return adsMapper.listAdsToAdsDto(adsList.size(), adsList);
    }

    /**
     * Добавляет объявление
     *
     * @param ads   объект {@link AdsDto}
     * @param image объект {@link MultipartFile}
     * @return объект {@link AdsDto}
     */
    @Override
    public AdsDto saveAd(CreateAdsDto ads, String email, MultipartFile image) {
        Ads saveAds = adsMapper.adsDtoToAds(ads);
        saveAds.setUser(userRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new));
        saveAds.setImage(image.getName()); // Todo продумать работу с image
        adsRepository.save(saveAds);
        return adsMapper.adsToAdsDto(saveAds);
    }

    /**
     * Получает информацию об объявлении
     *
     * @param id идентификатор объявления
     * @return объект {@link AdsDto}
     */
    @Override
    public FullAdsDto getAd(Integer id) {
        Ads ads = adsRepository.findById(id).orElseThrow(AdsNotFoundException::new);
        System.out.println(ads);
        return adsMapper.toFullAdsDto(ads);
    }

    /**
     * Удаляет объявление
     *
     * @param id идентификатор объявления
     * @return <code>true</code> если объявление удалено, <code>false</code> в случае неудачи
     */
    @Override
    public void removeAd(int id) {
        adsRepository.deleteById(id);
           /*или  мягкое удаление:
            ads.setDeleted(true);
            adsRepository.save(ads);*/
    }

    /**
     * Обновляет информацию об объявлении
     *
     * @param id           идентификатор объявления
     * @param createAdsDto новая информация об объявлении
     * @return объект {@link AdsDto}
     */
    @Override
    public AdsDto updateAds(int id, CreateAdsDto createAdsDto) {
        if (adsRepository.findById(id).isPresent()) {
            Ads ads = adsRepository.findById(id).get();
            adsMapper.updateAdsFromCreateAdsDto(createAdsDto, ads);
            adsRepository.save(ads);
            return adsMapper.adsToAdsDto(ads);
        }
        return null;
    }

    /**
     * Получает данные об объявлениях пользователя
     *
     * @param authentication данные о текущем пользователе
     * @return данные об объявлениях пользователя в виде дто-объекта {@link ResponseWrapperAdsDto}
     */
    @Override
    public ResponseWrapperAdsDto getAdsMe(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.getUserByEmailIgnoreCase(username).orElseThrow(UserNotFoundException::new);
        List<Ads> adsList = adsRepository.findAllByUser(user);
        System.out.println("Где ошибка то???");
        return adsMapper.listAdsToAdsDto(adsList.size(), adsList);
    }

    /**
     * Обновляет картинку объявления
     *
     * @param id    идентификатор объявления
     * @param image новая картинка
     * @return <code>true</code> если картинка обновлена, <code>false</code> в случае неудачи
     */
    @Override
    public boolean updateImage(int id, MultipartFile image) {
        return true;
    }
    @Override
    public boolean isThisUser(String email, int id){
        Ads ads = adsRepository.findById(id).orElseThrow(AdsNotFoundException::new);
        return email.equals(ads.getUser().getEmail());
    }

}
