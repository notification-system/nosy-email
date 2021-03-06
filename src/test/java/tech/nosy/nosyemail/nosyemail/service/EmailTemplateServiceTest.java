package tech.nosy.nosyemail.nosyemail.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tech.nosy.nosyemail.nosyemail.exceptions.*;
import tech.nosy.nosyemail.nosyemail.model.*;
import tech.nosy.nosyemail.nosyemail.repository.EmailTemplateRepository;
import tech.nosy.nosyemail.nosyemail.repository.InputSystemRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailTemplateServiceTest {

    @InjectMocks
    private EmailTemplateService emailTemplateServiceMock;
    @Mock
    private EmailTemplateRepository emailTemplateRepositoryMock;
    @Mock
    private InputSystemRepository inputSystemRepository;
    @Mock
    EmailService emailService;

    @Mock
    EmailConfigService emailConfigService;

    @Mock
    ReadyEmail readyEmail;
    private EmailProviderProperties emailProviderProperties;
    private String emailTemplateId;
    private String inputSystemId;
    private String email;
    private EmailTemplate emailTemplate;
    private InputSystem inputSystem;

    private void setVariables(){
        emailTemplateId="emailTemplateId";

        inputSystemId="inputSystemId";
        email="test@nosy.tech";
        emailTemplate=new EmailTemplate();
        emailTemplate.setEmailTemplateFromProvider(EmailFromProvider.DEFAULT);
        Set<String> emailsCc=new HashSet<>();
        emailsCc.add("testCc@nosy.tech");
        emailTemplate.setEmailTemplateCc(emailsCc);
        Set<String> emailsTo=new HashSet<>();
        emailsTo.add("testTo@nosy.tech");
        emailTemplate.setEmailTemplateId(emailTemplateId);
        emailTemplate.setEmailTemplateTo(emailsTo);
        emailTemplate.setEmailTemplateText("Test Message");
        emailTemplate.setEmailTemplateSubject("Test Subject");
        emailTemplate.setEmailTemplateName("Test Email Template Name");
        emailTemplate.setEmailTemplateRetryPeriod(1);
        emailTemplate.setEmailTemplatePriority(1);
        emailTemplate.setEmailTemplateFromAddress("testFromAddress@nosy.tech");

        inputSystem=new InputSystem();
        inputSystem.setInputSystemName("testInputSystem");
        inputSystem.setInputSystemId(inputSystemId);
        emailTemplate.setInputSystem(inputSystem);
        readyEmail=new ReadyEmail();
        readyEmail.setEmailTemplate(emailTemplate);
        emailProviderProperties=new EmailProviderProperties();
        emailProviderProperties.setPassword("TestPassword");
        List<PlaceHolder> placeHolders=new ArrayList<>();
        emailProviderProperties.setPlaceholders(placeHolders);
        emailProviderProperties.setUsername("Test");

        readyEmail.setEmailProviderProperties(emailProviderProperties);
    }

    @Before
    public void beforeEmailTemplate(){
        setVariables();
    }

    @Test
    public void getEmailTemplateByIdTest() {
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        Assert.assertEquals(emailTemplate.getEmailTemplateName(), emailTemplateServiceMock.getEmailTemplateByName
                (inputSystem.getInputSystemName(), emailTemplate.getEmailTemplateName(), email).getEmailTemplateName()
        );
        String emailTemplateString="EmailTemplate{emailTemplateId='emailTemplateId', emailTemplateName='Test Email Template Name', emailTemplateFromAddress='testFromAddress@nosy.tech', emailTemplateFromProvider=DEFAULT, emailTemplateTo=[testTo@nosy.tech], emailTemplateCc=[testCc@nosy.tech], emailTemplateText='Test Message', emailTemplateRetryTimes=0, emailTemplateRetryPeriod=1, emailTemplatePriority=1, emailTemplateSubject='Test Subject'}";
        assertEquals(emailTemplateString, emailTemplate.toString());

    }

    @Test(expected= EmailTemplateNotFoundException.class)
    public void getEmailTemplateByIdErrorTest() {
        doReturn(null).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        Assert.assertEquals(emailTemplateId, emailTemplateServiceMock.getEmailTemplateByName(inputSystem.getInputSystemName(),
                emailTemplate.getEmailTemplateName(), email).getEmailTemplateId()
        );
        String emailTemplateString="EmailTemplate{emailTemplateId='emailTemplateId', emailTemplateName='Test Email Template Name', fromAddress='testFromAddress@nosy.tech', emailTemplateTo=[testTo@nosy.tech], emailTemplateCc=[testCc@nosy.tech], text='Test Message', retryTimes=0, retryPeriod=1, priority=1, subject='Test Subject'}";
        assertEquals(emailTemplateString, emailTemplate.toString());

    }

    @Test
    public void newEmailTemplateTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).save(any());
        Assert.assertEquals(emailTemplate.getEmailTemplateName(), emailTemplateServiceMock.newEmailTemplate(emailTemplate,
                inputSystem.getInputSystemName(), email, null).getEmailTemplateName());
    }

    @Test(expected = EmailTemplateExistException.class)
    public void newEmailTemplateEmailTemplateExistsTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem(anyString(),
                any());
        Assert.assertEquals(emailTemplateId, emailTemplateServiceMock.
                newEmailTemplate(emailTemplate, inputSystemId, email, null).getEmailTemplateId());

    }
    @Test
    public void getAllEmailProvidersTest() {
        ArrayList<String> providers=new ArrayList<>();
        providers.add("DEFAULT");
        providers.add("YANDEX");
        providers.add("GMAIL");
        providers.add("CUSTOM");

        assertEquals(providers, emailTemplateServiceMock.getAllEmailProviders());
    }

    @Test
    public void deleteEmailTemplateTest() {
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        emailTemplateServiceMock.deleteEmailTemplate(inputSystemId, emailTemplateId, email);
        assertNotNull(inputSystemId);
    }

    @Test
    public void getListOfEmailTemplatesTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        List<EmailTemplate> listOfEmailTemplates=new ArrayList<>();
        listOfEmailTemplates.add(emailTemplate);
        when(emailTemplateRepositoryMock.findEmailTemplatesByInputSystem(inputSystem)).thenReturn(listOfEmailTemplates);
        assertEquals(listOfEmailTemplates,emailTemplateServiceMock.getListOfEmailTemplates(inputSystemId, email));
        assertEquals("testInputSystem", emailTemplate.getInputSystem().getInputSystemName());
        assertEquals("Test", emailProviderProperties.getUsername());

    }

    @Test(expected = InputSystemNotFoundException.class)
    public void getListOfEmailTemplatesInputSystemNotFoundTest() {
        doReturn(null).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        emailTemplateServiceMock.getListOfEmailTemplates(inputSystemId, email);
    }

    @Test(expected = InputSystemNotFoundException.class)
    public void getListEmailTemplatesListNullTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        when(inputSystemRepository.findInputSystemByEmailAndInputSystemName(email, inputSystem.getInputSystemName())).thenReturn(null);
        assertNull(emailTemplateServiceMock.getListOfEmailTemplates(inputSystem.getInputSystemName(), email));
    }

    @Test
    public void getListEmailTemplatesListEmptyTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        List<EmailTemplate> listOfEmailTemplates=new ArrayList<>();
        when(emailTemplateRepositoryMock.findEmailTemplatesByInputSystem(inputSystem)).thenReturn(listOfEmailTemplates);
        assertEquals(listOfEmailTemplates,emailTemplateServiceMock.getListOfEmailTemplates(inputSystemId, email));
    }

    @Test
    public void postEmailTemplateTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        assertEquals(emailTemplateId, emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderProperties, email).getEmailTemplateId());
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());

    }

    @Test
    public void postEmailTest() {
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }

    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithoutPasswordTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);

        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setUsername("tett");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());

        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());

    }
    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithoutUsernameTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        List<String> emailTo=new ArrayList<>();
        emailTo.add("oktay@gmail.com");
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dasdadsadad");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());


        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());

    }

    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithoutPasswordWithoutUsernameTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());


        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());

    }
    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithPasswordNullTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");

        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("");
        emailProviderPropertiesTest.setUsername("dafsaf");

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());


        Assert.assertEquals(emailTemplateId, emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email).getEmailTemplateId());
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }

    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithEmptyUsernameTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");

        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dafsaf");
        emailProviderPropertiesTest.setUsername("");

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());


        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());

    }


    @Test
    public void postEmailTemplateNonDefaultWithUsernameAndPasswordTest() {
        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dafsaf");
        emailProviderPropertiesTest.setUsername("fasdfad");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }


    @Test
    public void postEmailTemplateNonDefaultWithParameterProviderTest() {
        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dafsaf");
        emailProviderPropertiesTest.setUsername("fasdfad");
        PlaceHolder placeHolder=new PlaceHolder();

        emailTemplateForYandex.setEmailTemplateText("Hi #{name}#, #{body}#");
        placeHolder.setName("name");
        placeHolder.setValue("Testoktay");
        PlaceHolder placeHolder1=new PlaceHolder();
        placeHolder1.setName("body");
        placeHolder1.setValue("Hi");
        List<PlaceHolder> placeholdersList=new ArrayList<>();
        placeholdersList.add(placeHolder);
        placeholdersList.add(placeHolder1);
        emailProviderPropertiesTest.setPlaceholders(placeholdersList);
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());

        Assert.assertEquals("Hi Testoktay, Hi", emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId,
                emailProviderPropertiesTest, email).getEmailTemplateText());

        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }

    @Test(expected = NotEnoughParametersForPlaceholdersException.class)
    public void postEmailTemplateNonDefaultWithParameterProviderButNotEnoughReplacementsTest() {
        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dafsaf");
        emailProviderPropertiesTest.setUsername("fasdfad");
        PlaceHolder placeHolder=new PlaceHolder();

        emailTemplateForYandex.setEmailTemplateText("Hi #{name}# #{test}#, #{body}#");
        placeHolder.setName("name");
        placeHolder.setValue("Testoktay");
        PlaceHolder placeHolder1=new PlaceHolder();
        placeHolder1.setName("body");
        placeHolder1.setValue("Hi");
        List<PlaceHolder> placeholdersList=new ArrayList<>();
        placeholdersList.add(placeHolder);
        placeholdersList.add(placeHolder1);
        emailProviderPropertiesTest.setPlaceholders(placeholdersList);
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);

        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }


    @Test(expected = NotEnoughParametersForPlaceholdersException.class)
    public void postEmailTemplateNonDefaultWithParameterProviderButNotEnoughReplacementsClosingTagTest() {
        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");
        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("dafsaf");
        emailProviderPropertiesTest.setUsername("fasdfad");
        PlaceHolder placeHolder=new PlaceHolder();

        emailTemplateForYandex.setEmailTemplateText("Hi #{name}# }#, #{body}#");
        placeHolder.setName("name");
        placeHolder.setValue("Testoktay");
        PlaceHolder placeHolder1=new PlaceHolder();
        placeHolder1.setName("body");
        placeHolder1.setValue("Hi");
        List<PlaceHolder> placeholdersList=new ArrayList<>();
        placeholdersList.add(placeHolder);
        placeholdersList.add(placeHolder1);
        emailProviderPropertiesTest.setPlaceholders(placeholdersList);
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);

        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }
    @Test(expected = UsernameAndPasswordAreNotProvidedForNonDefaultException.class)
    public void postEmailTemplateNonDefaultWithUsernameNullTest() {

        EmailTemplate emailTemplateForYandex=new EmailTemplate();
        emailTemplateForYandex.setEmailTemplateName("Test");

        emailTemplateForYandex.setEmailTemplateFromProvider(EmailFromProvider.YANDEX);
        EmailProviderProperties emailProviderPropertiesTest=new EmailProviderProperties();
        emailProviderPropertiesTest.setPassword("");

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplateForYandex).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());


        emailTemplateServiceMock.postEmailTemplate(inputSystemId, emailTemplateId, emailProviderPropertiesTest, email);
        assertEquals("Test", readyEmail.getEmailProviderProperties().getUsername());
        assertEquals("Test Email Template Name", readyEmail.getEmailTemplate().getEmailTemplateName());
    }



    @Test
    public void updateEmailTemplateTest() {
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate, inputSystemId, emailTemplateId, email));
    }
    @Test
    public void updateEmailTemplateEmptyTemplateFromAddressTest() {
        emailTemplate.setEmailTemplateFromAddress("");

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate, inputSystemId, emailTemplateId, email));
    }
    @Test
    public void updateEmailTemplateNullTemplateFromAddressTest() {
        emailTemplate.setEmailTemplateFromAddress(null);

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate, inputSystemId, emailTemplateId, email));
    }


    @Test(expected = EmailTemplateNameInvalidException.class)
    public void updateEmailTemplateNullTemplateNameTest() {
        emailTemplate.setEmailTemplateName(null);
        emailTemplate.setEmailTemplateFromAddress(null);

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate, inputSystemId, emailTemplateId, email));
    }

    @Test(expected = EmailTemplateNameInvalidException.class)
    public void updateEmailTemplateEmptyTemplateNameTest() {
        emailTemplate.setEmailTemplateName("");
        emailTemplate.setEmailTemplateFromAddress(null);

        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(emailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate, inputSystemId, emailTemplateId, email));
    }
    @Test
    public void updateEmailTemplateNotEqualsToCurrentTest() {
        EmailTemplate emailTemplate1=new EmailTemplate();

        emailTemplate1.setEmailTemplateName("TestName");

        String currentEmailTemplateId="TestName";
        EmailTemplate currentEmailTemplate=new EmailTemplate();
        currentEmailTemplate.setEmailTemplateFromProvider(EmailFromProvider.DEFAULT);
        Set<String> emailsCc=new HashSet<>();
        emailsCc.add("testCc@nosy.tech");
        currentEmailTemplate.setEmailTemplateCc(emailsCc);
        Set<String> emailsTo=new HashSet<>();
        emailsTo.add("testTo@nosy.tech");
        currentEmailTemplate.setEmailTemplateId(currentEmailTemplateId);
        currentEmailTemplate.setEmailTemplateTo(emailsTo);
        currentEmailTemplate.setEmailTemplateText("Test Message");
        currentEmailTemplate.setEmailTemplateSubject("Test Subject");
        currentEmailTemplate.setEmailTemplateName("TestName");
        currentEmailTemplate.setEmailTemplateRetryPeriod(1);
        currentEmailTemplate.setEmailTemplatePriority(1);
        currentEmailTemplate.setEmailTemplateFromAddress("testFromAddress@nosy.tech");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(currentEmailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate1.getEmailTemplateName(),emailTemplateServiceMock.
                updateEmailTemplate(emailTemplate1, inputSystem.getInputSystemName(),
                        emailTemplate.getEmailTemplateName(), email).getEmailTemplateName());
    }
    @Test(expected = EmailTemplateNameInvalidException.class)
    public void updateEmailTemplateCurrentIsEmptyTest() {
        EmailTemplate emailTemplate1=new EmailTemplate();
        emailTemplate.setEmailTemplateName("");
        String currentEmailTemplateId="dasd";
        EmailTemplate currentEmailTemplate=new EmailTemplate();
        currentEmailTemplate.setEmailTemplateFromProvider(EmailFromProvider.DEFAULT);
        Set<String> emailsCc=new HashSet<>();
        emailsCc.add("testCc@nosy.tech");
        currentEmailTemplate.setEmailTemplateCc(emailsCc);
        Set<String> emailsTo=new HashSet<>();
        emailsTo.add("testTo@nosy.tech");
        currentEmailTemplate.setEmailTemplateId(currentEmailTemplateId);
        currentEmailTemplate.setEmailTemplateTo(emailsTo);
        currentEmailTemplate.setEmailTemplateText("Test Message");
        currentEmailTemplate.setEmailTemplateSubject("Test Subject");
        currentEmailTemplate.setEmailTemplateName("TestName");
        currentEmailTemplate.setEmailTemplateRetryPeriod(1);
        currentEmailTemplate.setEmailTemplatePriority(1);
        currentEmailTemplate.setEmailTemplateFromAddress("testFromAddress@nosy.tech");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(currentEmailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate1, inputSystemId, emailTemplateId, email));
    }
    @Test(expected = EmailTemplateNotFoundException.class)
    public void updateEmailTemplateNullTest() {
        EmailTemplate emailTemplate1=null;

        String currentEmailTemplateId="dasd";
        EmailTemplate currentEmailTemplate=new EmailTemplate();
        currentEmailTemplate.setEmailTemplateFromProvider(EmailFromProvider.DEFAULT);
        Set<String> emailsCc=new HashSet<>();
        emailsCc.add("testCc@nosy.tech");
        currentEmailTemplate.setEmailTemplateCc(emailsCc);
        Set<String> emailsTo=new HashSet<>();
        emailsTo.add("testTo@nosy.tech");
        currentEmailTemplate.setEmailTemplateId(currentEmailTemplateId);
        currentEmailTemplate.setEmailTemplateTo(emailsTo);
        currentEmailTemplate.setEmailTemplateText("Test Message");
        currentEmailTemplate.setEmailTemplateSubject("Test Subject");
        currentEmailTemplate.setEmailTemplateName("TestName");
        currentEmailTemplate.setEmailTemplateRetryPeriod(1);
        currentEmailTemplate.setEmailTemplatePriority(1);
        currentEmailTemplate.setEmailTemplateFromAddress("testFromAddress@nosy.tech");
        doReturn(inputSystem).when(inputSystemRepository).findInputSystemByEmailAndInputSystemName(anyString(), anyString());
        doReturn(currentEmailTemplate).when(emailTemplateRepositoryMock).findEmailTemplateByEmailTemplateNameAndInputSystem
                (anyString(), any());
        Assert.assertEquals(emailTemplate,emailTemplateServiceMock.updateEmailTemplate(emailTemplate1, inputSystemId, emailTemplateId, email));
    }

    @Test
    public void setEmailConfigTest(){
        emailTemplate.setEmailTemplateFromProvider(EmailFromProvider.CUSTOM);
        EmailConfig emailConfig=new EmailConfig();
        emailConfig.setEmailConfigId("asdasd");
        emailConfig.setEmail("dasdasd");
        emailConfig.setHost("dada");
        emailConfig.setEmailConfigName("dadasda");
        emailConfig.setPort(3231);
        emailTemplateServiceMock.setEmailConfig(emailTemplate,emailConfig.getEmail(), emailConfig.getEmailConfigName());
    }

    @Test(expected = CustomEmailConfigShouldNotBeEmptyException.class)
    public void setEmailConfigNullTest(){
        emailTemplate.setEmailTemplateFromProvider(EmailFromProvider.CUSTOM);
        EmailConfig emailConfig=null;

        emailTemplateServiceMock.setEmailConfig
                (emailTemplate,"emailConfig", null);
    }
}
