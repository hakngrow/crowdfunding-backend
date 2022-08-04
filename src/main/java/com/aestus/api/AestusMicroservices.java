package com.aestus.api;

import com.aestus.api.product.model.Product;
import com.aestus.api.product.repository.ProductRepository;
import com.aestus.api.profile.model.UserProfile;
import com.aestus.api.profile.repository.ProfileRepository;
import com.aestus.api.request.model.Request;
import com.aestus.api.request.repository.RequestRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** The Aestus user profile microservice. */
@Slf4j
@SpringBootApplication
public class AestusMicroservices {

  @Value("${spring.profiles.active:}")
  private final String activeProfile = null;

  @Value("${server.port}")
  private final String serverPort = null;

  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired ProfileRepository profileRepository;

  @Autowired ProductRepository productRepository;
  @Autowired RequestRepository requestRepository;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {

    RestTemplate restTemplate = builder.build();

    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

    return restTemplate;
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(AestusMicroservices.class, args);
  }

  private UserProfile[] getProfiles() {

    LocalDateTime registrationDate = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);

    UserProfile profile1 =
        new UserProfile(
            "sbipcc",
            "11110101",
            "SBIP",
            "Community Clinic",
            "sbip-clinic@gmail.com",
            "98760001",
            "U",
            registrationDate,
            "4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se");

    UserProfile profile2 =
        new UserProfile(
            "toraymed",
            "22220202",
            "Toray",
            "Medical",
            "toray@gmail.com",
            "98760002",
            "S",
            registrationDate,
            "SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU");

    UserProfile profile3 =
        new UserProfile(
            "samsung",
            "33330303",
            "Samsung",
            "Healthcare",
            "samsung@gmail.com",
            "98760003",
            "S",
            registrationDate,
            "5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK");

    UserProfile profile4 =
        new UserProfile(
            "nusepe",
            "44440404",
            "NUS Enterprise",
            "Private Equity",
            "nusepe@gmail.com",
            "98760004",
            "I",
            registrationDate,
            "6FVgAaLL8avbKAVWLB64sZJ3j8zJToiuNZFbvpHBrzFc");

    UserProfile profile5 =
        new UserProfile(
            "peterlim",
            "55550505",
            "Peter",
            "Lim",
            "peter@gmail.com",
            "98760005",
            "I",
            registrationDate,
            "qJJZvUgCRtJMNHqq91EcoStYw8NhWyszrtWRVLhVmw4");

    UserProfile profile6 =
        new UserProfile(
            "sbipadmin",
            "66660606",
            "SBIP",
            "Admin",
            "sbip@gmail.com",
            "98760006",
            "A",
            registrationDate,
            "CbgZoPiQnnASxoYnerEAumW67EJrVkcHcCNeLCQbdBNA");

    profile1.setPassword(passwordEncoder.encode(profile1.getPassword()));
    profile2.setPassword(passwordEncoder.encode(profile2.getPassword()));
    profile3.setPassword(passwordEncoder.encode(profile3.getPassword()));
    profile4.setPassword(passwordEncoder.encode(profile4.getPassword()));
    profile5.setPassword(passwordEncoder.encode(profile5.getPassword()));
    profile6.setPassword(passwordEncoder.encode(profile6.getPassword()));

    return new UserProfile[] {profile1, profile2, profile3, profile4, profile5, profile6};
  }

  private Request[] getRequests() {

    LocalDateTime timestamp = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);

    Request request1 =
        new Request(
            1,
            2,
            null,
            "Epson T3-B All-in-One SCARA Robot",
            "RFP",
            "O",
            "The ideal alternative to slide-based solutions, All-in-One design includes power for "
                + "end-of-arm tooling, 4 built-in axes in one compact design. Perfect for pick and place, simple "
                + "assembly, material handing and dispensing.",
            1000000L,
            null,
            "Designed to seamlessly fit in a variety of workspaces, this all-in-one solution features "
                + "a built-in controller, power for end-of-arm tooling and 110 V or 220 V power—virtually "
                + "eliminating any space-constraint issues. Plus, it offers a 400 mm reach and a payload of "
                + "up to 3 kg to easily handle a variety of tasks.",
            timestamp);

    Request request2 =
        new Request(
            1,
            3,
            null,
            "Epson VT6L All-in-One 6-Axis Robot",
            "RFP",
            "C",
            "Features Slimline design perfect for factories with limited floor space and compact wrist"
                + " pitch that enables robot easy access to hard-to-reach areas. Ideal for load/ unload, "
                + "packaging or parts assembly applications. Cleanroom (ISO4) and Protected (IP67) models "
                + "available.",
            2000000L,
            25000000L,
            "VT6L offers a reach up to 900 mm and a payload up to 6 kg. A feature-packed performer, "
                + "it includes a built-in controller, plus simplified cabling with a hollow end-of-arm "
                + "design – all at a remarkably low cost, in a compact, SlimLine structure. The VT6L offers "
                + "110 V and 220 V power and installs in minutes.",
            timestamp);

    Request request3 =
        new Request(
            2,
            1,
            1,
            "TX2touch-90 POWER Cobot",
            "PRO",
            "O",
            "TX2touch is the only cobot with the SIL3/PLe safety level. It is highly productive due "
                + "to the performance, smart connectivity and reliability inherited from TX2 robots and "
                + "its CS9 controller.",
            3000000L,
            31000000L,
            "The TX2touch-90 series has a payload of up to 10 kg with a maximum reach of 1450 mm.",
            timestamp);

    Request request4 =
        new Request(
            4,
            1,
            1,
            "DOBOT MG400 Lightweight Desktop Robotic Arm",
            "PRO",
            "O",
            "DOBOT MG400 is a lightweight space-saving desktop robotic arm suitable for diversified "
                + "manufacturing needs. It is flexible to deploy and easy to use, perfect for small space "
                + "applications. MG400 is a good fit for automated workbench scenarios in tight workspaces "
                + "that require fast deployment and changeover.",
            2888000L,
            null,
            "With the footprint dimension of 190mm, MG400 can fit in any production environment smaller "
                + "than one piece of A4 paper and free up more space in the plant for production. It is the perfect "
                + "fit for repeating lightweight tasks and automated workbench scenarios in tight workspaces. The "
                + "compact desktop collaborative robot weighs only 8kg but has a payload up to 750g.",
            timestamp);

    return new Request[] {request1, request2, request3, request4};
  }

  private Product[] getMedicalProducts() {

    LocalDateTime timestamp = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);

    Product product1 =
        new Product(
            2,
            "Single Patient Dialysis Machine TR-8000",
            "Dialysis",
            "The single patients dialysis machine can perform prescribed dialysis which adjust the "
                + "dialysate conductivity in accordance with each patient. It mainly consists of Monitor/alarm "
                + "part, Dialysate supply/UF control part, Extracorporeal blood circuit part, and Electrical control "
                + "part.",
            "http://localhost:" + this.serverPort + "/images/dialysis.jpg",
            1200000L,
            "Supported by the advanced technologies of TORAY, TR-8000 offers comfortable dialysis "
                + "treatment to patients, easy operation to medical staff and contributes to medical development.\n"
                + "\n"
                + "- Easy operability by changing the position of external parts\n"
                + "- The position of rinse ports, couplers and bicarbonate cartridge was changed and you can "
                + "operate them without squat.\n"
                + "- Casters became larger and you can move it by less power.\n"
                + "Improvement of standard functions\n"
                + "- The chamber level adjustment has been newly equipped as standard\n"
                + "- Self-test time became shorter\n"
                + "Various new options and accessories\n"
                + "- BVM, Kt/V indicator, Bicarbonate cartridge, etc.",
            timestamp);

    Product product2 =
        new Product(
            2,
            "Infusomat® Space",
            "Pumps",
            "The Infusomat® Space pump is the volumetric infusion pump solution to configure customized, "
                + "tailor-made solutions as individual pumps, small therapy units, or a complex infusion system.\n"
                + "The small, light and intuitive configuration of the pump provides a userfriendly solution to "
                + "complex therapies, integrating all clinical areas into one system.",
            "http://localhost:" + this.serverPort + "/images/infusion-pump.jpg",
            600000L,
            "- Light weight pump (1.4kg) and reliable battery duration of 8hours on a 25 ml/h rate\n"
                + "- Therapy Profiles available : Dose Rate Calculation, Dose\n"
                + "- Over Time, Ramp and Taper Mode, Program Mode,\n"
                + "- Intermittent Mode, PCA, TCI, TIVA, KVO and Piggyback function\n"
                + "- Set and pump based Anti Free-Flow mechanism, protecting against free flow\n"
                + "- Drug Library with capacity to up to 1200 drug names, including therapy data, information and "
                + "up to 10 concentrations per drug to be stored in 30 different categories. Drug information can "
                + "be subdivided in 50 care units and up to 16 patient profiles.",
            timestamp);

    Product product3 =
        new Product(
            3,
            "Samsung HS40 Ultrasound Machine",
            "Ultrasound",
            "The Samsung HS40 is an economy multi-purpose diagnostic ultrasound system designed to cover "
                + "a wide range of applications including obstetrics, abdominal, gynecology, pediatric, small "
                + "organs, neonatal cephalic, adult cephalic, trans-rectal, trans-vaginal, MSK (conventional, "
                + "superficial), urology, adult cardiac, and peripheral vessel.",
            "http://localhost:" + this.serverPort + "/images/ultrasound.jpg",
            2800000L,
            "The Samsung HS40, similarly to the HS60 and HS50, is empowered by the “S-Vision Imaging "
                + "Engine” featuring 64 transmitting channels, and is equipped with a number of high performing "
                + "hardware such as a 21.5″ LED monitor, a fully customizable 10.1″ LED touchscreen, 3 active "
                + "probe ports (with a 4th as an option), convex and endocavity volume transducers (VN4-8, V5-9), "
                + "nd a broad bandwidth linear array transducer (LA3-16A). These transducers effectively cover a "
                + "wide array of clinical requirements, thanks to their low to high bandwidth frequency coverage.",
            timestamp);

    Product product4 =
        new Product(
            3,
            "Multix Select DR",
            "X-ray",
            "Multix Select DR is a floor-mounted digital X-ray machine that combines cost-effective room "
                + "setup with a high level of clinical flexibility. Selected top-of-the-line technologies provide "
                + "outstanding image quality. Achieve more financial flexibility with a digital X-ray machine that "
                + "offers expert solutions for general radiography in an economical way.",
            "http://localhost:" + this.serverPort + "/images/x-ray.png",
            7200000L,
            "Patient table2 and tabletop\n"
                + "- Table height: 75 cm (29.5\")\n"
                + "- Tabletop width: 80 cm (31.5\")\n"
                + "- Tabletop length: 235 cm (92.5\")\n"
                + "- Max. patient weight: 200 kg (441 lbs.)\n"
                + "- Longitudinal tabletop travel : ± 46 cm (± 18.1\")"
                + "Column stand\n"
                + "- Horizontal travel range: 138 cm (54.3\"), movement arrested by electromagnetic brakes3\n"
                + "- Vertical travel range: 150 cm (59.1\"), movement arrested by electromagnetic brakes\n"
                + "- Central beam height: 35 cm (13.8\") to 185 cm (72.8\")\n"
                + "- Rotation of tube around vertical axis: ± 90°; stop positions: 0°, ± 90°\n"
                + "- X-ray tube rotation: ± 120°; stop positions: 0°, ± 90°",
            timestamp);

    Product product5 =
        new Product(
            3,
            "Vista 120 Central Monitoring System (CMS)",
            "Monitoring",
            "The easy-to-use Vista 120 Central Monitoring System (CMS) lets you centrally monitor the "
                + "vital signs of up to 64 patients connected to Vista 120/Vista 120 S bedside monitors. This "
                + "central surveillance streamlines workflow for clinicians, while significantly increasing "
                + "patient safety.",
            "http://localhost:" + this.serverPort + "/images/patient-monitoring.jpg",
            3400000L,
            "The Vista 120 S displays up to eleven waveforms in an easy-to-configure layout and offers a "
                + "core set of essential parameters including 3/5 lead ECG, non-invasive blood pressure, respiration "
                + "and dual temperature comes standard. Advanced parameters including three invasive blood pressures, "
                + "mainstream etCO2, cardiac output and sidestream etCO2 are also available.\n"
                + "Users are free to add external parameter modules including SCIO for all models and CO2 on model "
                + "C/C+ after initial device purchase.",
            timestamp);

    return new Product[] {product1, product2, product3, product4, product5};
  }

  @PostConstruct
  public void init() {
    for (UserProfile profile : getProfiles()) profileRepository.save(profile);
    for (Product product : getMedicalProducts()) productRepository.save(product);
    // for (Request request : getRequests()) requestRepository.save(request);
  }
}
