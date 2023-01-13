import csv
import mysql.connector
import datetime
import schedule
import time


def insertjob():
    Date = []
    Move_Minutes_count = []
    Calories = []
    Distance = []
    HeartPoints = []
    Average_heart_rate = []
    Max_heart_rate = []
    Min_heart_rate = []
    Average_oxygen_saturation = []
    Max_oxygen_saturation = []
    Min_oxygen_saturation = []
    Average_supplemental_oxygen_flow_rate = []
    Max_supplemental_oxygen_flow_rate = []
    Min_supplemental_oxygen_flow_rate = []
    Oxygen_therapy_administration_mode = []
    Oxygen_saturation_system = []
    Oxygen_saturation_measurement_method = []
    Average_speed = []
    Max_speed = []
    Min_speed = []
    Step_count = []
    Walking_duration = []
    Running_duration = []

    # --------------------------------------------------------------------
    # 連接CSV檔
    path = 'D:\Google Fit\Daily activity metrics\Daily activity metrics.csv'
    with open(path, newline='') as csvfile:
        rows = csv.reader(csvfile, delimiter=',')

        firstline = True
        for row in rows:
            if (firstline != True):  #跳過第一行
                Date.append(row[1])
                Move_Minutes_count.append(row[2])
                Calories.append(row[3])
                Distance.append(row[4])
                HeartPoints.append(row[5])
                Average_heart_rate.append(row[6])
                Max_heart_rate.append(row[7])
                Min_heart_rate.append(row[8])
                Average_oxygen_saturation.append(row[9])
                Max_oxygen_saturation.append(row[10])
                Min_oxygen_saturation.append(row[11])
                Average_supplemental_oxygen_flow_rate.append(row[12])
                Max_supplemental_oxygen_flow_rate.append(row[13])
                Min_supplemental_oxygen_flow_rate.append(row[14])
                Oxygen_therapy_administration_mode.append(row[15])
                Oxygen_saturation_system.append(row[16])
                Oxygen_saturation_measurement_method.append(row[17])
                Average_speed.append(row[18])
                Max_speed.append(row[19])
                Min_speed.append(row[20])
                Step_count.append(row[21])
                Walking_duration.append(row[22])
                Running_duration.append(row[23])
            else:
                firstline = False

    # --------------------------------------------------------------------
    # 連接資料庫
    connection = mysql.connector.connect(host='127.0.0.1',
                                         port='3306',
                                         user='root',
                                         password='alvin0722',
                                         database='googlefit')

    # --------------------------------------------------------------------
    # 每天早晚8:00自動把CSV導入資料庫
    cursor = connection.cursor()
    firstline = True
    for i in range(len(Date)):
        cursor.execute("INSERT INTO `googlefit` VALUES(Date[i], Move_Minutes_count[i], Calories[i], Distance[i], HeartPoints[i], Average_heart_rate[i],Max_heart_rate[i], Min_heart_rate[i], Average_oxygen_saturation[i],Max_oxygen_saturation[i], Min_oxygen_saturation[i],Average_supplemental_oxygen_flow_rate[i], Max_supplemental_oxygen_flow_rate[i], Min_supplemental_oxygen_flow_rate[i], Oxygen_therapy_administration_mode[i], Oxygen_saturation_system[i], Oxygen_saturation_measurement_method[i], Average_speed[i], Max_speed[i], Min_speed[i],Step_count[i], Walking_duration[i], Running_duration[i])")

    cursor.close()
    connection.close()


insertjob()
# schedule.every().days.at('8:00').do(insertjob)
# schedule.every().days.at('6:55').do(insertjob)
