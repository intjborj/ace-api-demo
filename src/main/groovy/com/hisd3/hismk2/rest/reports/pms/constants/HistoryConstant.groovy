package com.hisd3.hismk2.rest.reports.pms.constants

import groovy.transform.Canonical

@Canonical
class PhysicalExamDto {
    String category
    String description
}

@Canonical
class DataPertinentExamDto{
    String category
    String description
}


interface HistoryConstant {

    Map<String,PhysicalExamDto> physicalExam = new HashMap<String,PhysicalExamDto>(){{
        put('awakealert',new PhysicalExamDto('General Survey','Awake and alert'))
        put('alteredsensorium',new PhysicalExamDto('General Survey','Altered sensorium'))
        put('othersgs',new PhysicalExamDto('General Survey','Others'))
        put('essentiallynormal',new PhysicalExamDto('HEENT','Essentially normal'))
        put('abnormalpupillaryreaction',new PhysicalExamDto('HEENT','Abnormal Pupillary reaction'))
        put('cervicallymphadenopthy',new PhysicalExamDto('HEENT','Cervical lymphadenopathy'))
        put('drymucousmembrane',new PhysicalExamDto('HEENT','Dry mucous membrane'))
        put('ictericsclerae',new PhysicalExamDto('HEENT','Icteric sclerae'))
        put('paleconjunctivae',new PhysicalExamDto('HEENT','Pale conjunctivae'))
        put('sunkenfontanelle',new PhysicalExamDto('HEENT','Sunken fontanelle'))
        put('others',new PhysicalExamDto('HEENT','Others'))

        put('essentiallynormal2', new PhysicalExamDto('chestLungs','Essentially normal'))
        put('asymmetricalchestexpansion', new PhysicalExamDto('chestLungs','Asymmetrical chest expansion'))
        put('decreasedbreathsounds',new PhysicalExamDto('Decreased breath sounds'))
        put('wheezes', new PhysicalExamDto('chestLungs','Wheezes'))
        put('lumpsoverbreasts', new PhysicalExamDto('chestLungs','Lumps over breast(s)'))
        put('ralescracklesrhonchi', new PhysicalExamDto('chestLungs','Rales/crackles/rhonchi'))
        put('intercostalribclavicularretraction', new PhysicalExamDto('chestLungs','Intercostal rib/clavicular retraction'))
        put('others2', new PhysicalExamDto('chestLungs','Others'))

        put('essentiallynormal3',new PhysicalExamDto('CVS','Essentially normal'))
        put('displacedapexbeat',new PhysicalExamDto('CVS','Displaced apex beat'))
        put('heavesthrills',new PhysicalExamDto('CVS','Heaves and/or Thrills'))
        put('pericardialbulge',new PhysicalExamDto('CVS','Pericardial Bulge'))
        put('irregularrhythm',new PhysicalExamDto('CVS','Irregular Rhythm'))
        put('muffledheartsounds',new PhysicalExamDto('Muffled heart sounds'))
        put('murmur',new PhysicalExamDto('CVS','Murmur'))
        put('others3',new PhysicalExamDto('CVS','Others'))

        put('essentiallynormal4', new PhysicalExamDto('abdomen','Essentially normal'))
        put('abdominalrigidity', new PhysicalExamDto('abdomen','Abdominal Rigidity'))
        put('abdomentenderness', new PhysicalExamDto('abdomen','Abdomen Tenderness'))
        put('hyperactivebowelsounds', new PhysicalExamDto('abdomen','Hyperactive bowel sounds'))
        put('palpablemass', new PhysicalExamDto('abdomen','Tympanitic/dull abdomen'))
        put('uterinecontraction', new PhysicalExamDto('abdomen','Uterine contraction'))
        put('others31', new PhysicalExamDto('abdomen','Others'))

        put('essentiallynormal5',new PhysicalExamDto('gu','Essentially normal' ))
        put('bloodstainedexamfinger',new PhysicalExamDto('gu','Blood stained in exam finger' ))
        put('cervicaldilatation',new PhysicalExamDto('gu','Cervical dilatation' ))
        put('presenceabnormaldischarge',new PhysicalExamDto('gu','Presence of Abnormal Discharge' ))
        put('others4',new PhysicalExamDto('gu','Others'))

        put('essentiallynormal6', new PhysicalExamDto('skin','Essentially normal'))
        put('clubbing', new PhysicalExamDto('skin','Clubbing'))
        put('coldclammyskin', new PhysicalExamDto('skin','Cold clammy skin'))
        put('cyanosismottledskin', new PhysicalExamDto('skin','Cyanosis/mottled skin'))
        put('edemaswelling', new PhysicalExamDto('skin','Edema/swelling'))
        put('decreasedmobility', new PhysicalExamDto('skin','Decreased mobility '))
        put('palenailbeds', new PhysicalExamDto('skin','Pale nailbeds'))
        put('poorskinturgor', new PhysicalExamDto('skin','Poor skin turgor'))
        put('rashespetechiae', new PhysicalExamDto('skin','Rashes/petechiae '))
        put('weakpulses', new PhysicalExamDto('skin','Weak pulses'))
        put('others5', new PhysicalExamDto('skin','Others'))

        put('essentiallynormal7', new PhysicalExamDto('neuro','Essentially normal'))
        put('abnormalgait', new PhysicalExamDto('neuro','Abnormal gait'))
        put('abnormalpositionsense', new PhysicalExamDto('neuro','Abnormal position sense'))
        put('abnormaldecreasedsense', new PhysicalExamDto('neuro','Abnormal/decreased sensation'))
        put('abnormalreflex', new PhysicalExamDto('neuro','Abnormal reflexput(es)'))
        put('pooralteredmemory', new PhysicalExamDto('neuro','Poor/altered memory'))
        put('poormuscletone', new PhysicalExamDto('neuro','Poor muscle tone/strength'))
        put('poorcoordination', new PhysicalExamDto('neuro','Poor coordination'))
        put('others6', new PhysicalExamDto('neuro','Others'))

    }};

    Map<String, DataPertinentExamDto> pertinentExam = new HashMap<String, DataPertinentExamDto>(){{
        put('alteredmentalsensorium', new DataPertinentExamDto('pertinentSymptoms','Altered mental sensorium'))
        put('abdominalcramppain', new DataPertinentExamDto('pertinentSymptoms','Abdominal cramp/pain'))
        put('anorexia', new DataPertinentExamDto('pertinentSymptoms','Anorexia'))
        put('bleedinggums', new DataPertinentExamDto('pertinentSymptoms','Bleeding gums'))
        put('bodyweakness', new DataPertinentExamDto('pertinentSymptoms','Body weakness'))
        put('blurringofvision', new DataPertinentExamDto('pertinentSymptoms','Blurring of vision'))
        put('chestpaindiscomfort', new DataPertinentExamDto('pertinentSymptoms','Chest pain/discomfort'))
        put('constipation', new DataPertinentExamDto('pertinentSymptoms','Constipation'))
        put('cough', new DataPertinentExamDto('pertinentSymptoms','Cough'))
        put('diarrhea', new DataPertinentExamDto('pertinentSymptoms','Diarrhea'))
        put('dizziness', new DataPertinentExamDto('pertinentSymptoms','Dizziness'))
        put('dysphagia', new DataPertinentExamDto('pertinentSymptoms','Dysphagia'))
        put('dyspnea', new DataPertinentExamDto('pertinentSymptoms','Dyspnea'))
        put('dysuria', new DataPertinentExamDto('pertinentSymptoms','Dysuria'))
        put('epistaxis', new DataPertinentExamDto('pertinentSymptoms','Epistaxis'))
        put('fever', new DataPertinentExamDto('pertinentSymptoms','Fever'))
        put('frequencyofurination', new DataPertinentExamDto('pertinentSymptoms','Frequency of urination'))
        put('headache', new DataPertinentExamDto('pertinentSymptoms','Headache'))
        put('hematemesis', new DataPertinentExamDto('pertinentSymptoms','Hematemesis'))
        put('hematuria', new DataPertinentExamDto('pertinentSymptoms','Hematuria'))
        put('hemoptysis', new DataPertinentExamDto('pertinentSymptoms','Hemoptysis'))
        put('irritability', new DataPertinentExamDto('pertinentSymptoms','Irritability'))
        put('jaundice', new DataPertinentExamDto('pertinentSymptoms','Jaundice'))
        put('lowerextremityedema', new DataPertinentExamDto('pertinentSymptoms','Lower extremity edema'))
        put('myalgia', new DataPertinentExamDto('pertinentSymptoms','Myalgia'))
        put('orthopnea', new DataPertinentExamDto('pertinentSymptoms','Orthopnea'))
        put('palpitations', new DataPertinentExamDto('pertinentSymptoms','Palpitations'))
        put('seizures', new DataPertinentExamDto('pertinentSymptoms','Seizures'))
        put('skinrashes', new DataPertinentExamDto('pertinentSymptoms','Skin rashes'))
        put('stoolbloodyblacktarrymucoid', new DataPertinentExamDto('pertinentSymptoms','Stool, bloody/black tarry/mucoid'))
        put('sweating', new DataPertinentExamDto('pertinentSymptoms','Sweating'))
        put('urgency', new DataPertinentExamDto('pertinentSymptoms','Urgency'))
        put('vomiting', new DataPertinentExamDto('pertinentSymptoms','Vomiting'))
        put('weightloss', new DataPertinentExamDto('pertinentSymptoms','Weight loss'))
        put('pertinentothers', new DataPertinentExamDto('pertinentSymptoms','Others'))
    }}
}