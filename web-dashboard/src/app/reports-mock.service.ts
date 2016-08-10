import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ReportsMockService {

  constructor() {
  }

  getAggregatedStats(): Observable<{usersStats: any}> {
    return Observable
      .from([{
        "usersStats": {
          "tasomaniac": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {
              "gradle-android-command-plugin": 3,
              "spikes": 8,
              "notils": 4,
              "oddschecker-android": 3,
              "novoda": 2
            },
            "externalRepositoriesContributions": 20
          },
          "takecare": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 253},
            "assignedProjectsContributions": 253,
            "externalRepositoriesStats": {
              "all-4": 17,
              "sqlite-provider": 6,
              "github-reports": 5,
              "spikes": 9,
              "project-d": 4,
              "piriform-ccleaner": 3,
              "oddschecker-android": 11
            },
            "externalRepositoriesContributions": 55
          },
          "Mecharyry": {
            "assignedProjectsStats": {"The Times: Scheduled": 252},
            "assignedProjectsContributions": 252,
            "externalRepositoriesStats": {"spikes": 6, "simple-chrome-custom-tabs": 2, "project-d": 9},
            "externalRepositoriesContributions": 17
          },
          "alexstyl": {
            "assignedProjectsStats": {"OddsChecker: Verified": 128, "OddsChecker: Scheduled": 128},
            "assignedProjectsContributions": 256,
            "externalRepositoriesStats": {"all-4": 8, "spikes": 15},
            "externalRepositoriesContributions": 23
          },
          "florianmski": {
            "assignedProjectsStats": {"Creators: Scheduled": 161},
            "assignedProjectsContributions": 161,
            "externalRepositoriesStats": {"all-4": 9, "spikes": 9, "soundcloud-creators": 16},
            "externalRepositoriesContributions": 34
          },
          "dominicfreeston": {
            "assignedProjectsStats": {"OddsChecker: Scheduled": 167},
            "assignedProjectsContributions": 167,
            "externalRepositoriesStats": {"spikes": 47, "oddschecker-ios": 2},
            "externalRepositoriesContributions": 49
          },
          "wltrup": {
            "assignedProjectsStats": {"OddsChecker: Verified": 59, "OddsChecker: Scheduled": 98},
            "assignedProjectsContributions": 157,
            "externalRepositoriesStats": {"oddschecker-ios": 29, "oddschecker-android": 2},
            "externalRepositoriesContributions": 31
          },
          "rock3r": {
            "assignedProjectsStats": {"All4: Verified": 906},
            "assignedProjectsContributions": 906,
            "externalRepositoriesStats": {
              "github-reports": 5,
              "merlin": 22,
              "download-manager": 2,
              "spikes": 16,
              "novoda": 1
            },
            "externalRepositoriesContributions": 46
          },
          "mr-archano": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {"spikes": 15, "soundcloud-creators": 6},
            "externalRepositoriesContributions": 21
          },
          "amlcurran": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {
              "all-4": 3,
              "spikes": 2,
              "oddschecker-apiary": 4,
              "oddschecker-ios": 42,
              "oddschecker-android": 50
            },
            "externalRepositoriesContributions": 101
          },
          "gbasile": {
            "assignedProjectsStats": {"OddsChecker: Verified": 43},
            "assignedProjectsContributions": 43,
            "externalRepositoriesStats": {"spikes": 11, "oddschecker-ios": 32},
            "externalRepositoriesContributions": 43
          },
          "blundell": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 53},
            "assignedProjectsContributions": 53,
            "externalRepositoriesStats": {
              "all-4": 28,
              "sqlite-provider": 4,
              "gradle-android-command-plugin": 2,
              "download-manager": 1,
              "merlin": 1,
              "spikes": 56,
              "snowy-village-wallpaper": 1,
              "notils": 1,
              "piriform-ccleaner": 1,
              "bonfire-firebase-sample": 1,
              "oddschecker-android": 5,
              "soundcloud-creators": 2,
              "novoda": 3
            },
            "externalRepositoriesContributions": 106
          },
          "yvettecook": {
            "assignedProjectsStats": {"OddsChecker: Verified": 26, "OddsChecker: Scheduled": 42},
            "assignedProjectsContributions": 68,
            "externalRepositoriesStats": {"spikes": 25, "oddschecker-ios": 9},
            "externalRepositoriesContributions": 34
          },
          "danybony": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 62},
            "assignedProjectsContributions": 62,
            "externalRepositoriesStats": {
              "all-4": 9,
              "github-reports": 19,
              "spikes": 45,
              "aosp.changelog.to": 8,
              "snowy-village-wallpaper": 4,
              "oddschecker-android": 49
            },
            "externalRepositoriesContributions": 134
          },
          "frapontillo": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 352},
            "assignedProjectsContributions": 352,
            "externalRepositoriesStats": {
              "sqlite-provider": 11,
              "github-reports": 85,
              "merlin": 7,
              "spikes": 30,
              "aosp.changelog.to": 1,
              "snowy-village-wallpaper": 2
            },
            "externalRepositoriesContributions": 136
          },
          "qqipp": {
            "assignedProjectsStats": {"OddsChecker: Scheduled": 1},
            "assignedProjectsContributions": 1,
            "externalRepositoriesStats": {"piriform-ccleaner": 2},
            "externalRepositoriesContributions": 2
          },
          "tobiasheine": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {"piriform-ccleaner": 4},
            "externalRepositoriesContributions": 4
          },
          "joetimmins": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 8},
            "assignedProjectsContributions": 8,
            "externalRepositoriesStats": {
              "sqlite-provider": 4,
              "merlin": 33,
              "spikes": 25,
              "notils": 1,
              "piriform-ccleaner": 1,
              "novoda": 3
            },
            "externalRepositoriesContributions": 67
          },
          "ouchadam": {
            "assignedProjectsStats": {"All4: Verified": 472, "All4: Scheduled": 472},
            "assignedProjectsContributions": 944,
            "externalRepositoriesStats": {
              "sqlite-provider": 2,
              "github-reports": 4,
              "gradle-android-command-plugin": 8,
              "spikes": 38,
              "snowy-village-wallpaper": 12,
              "soundcloud-creators": 2
            },
            "externalRepositoriesContributions": 66
          },
          "fourlastor": {
            "assignedProjectsStats": {"All4: Verified": 657, "All4: Scheduled": 657},
            "assignedProjectsContributions": 1314,
            "externalRepositoriesStats": {"merlin": 8, "spikes": 12, "oddschecker-android": 28},
            "externalRepositoriesContributions": 48
          },
          "eduardourso": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {"oddschecker-ios": 3},
            "externalRepositoriesContributions": 3
          },
          "Hutch4": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {"all-4": 1, "piriform-ccleaner": 150},
            "externalRepositoriesContributions": 151
          },
          "Dorvaryn": {
            "assignedProjectsStats": {"R \u0026 D: Scheduled": 15},
            "assignedProjectsContributions": 15,
            "externalRepositoriesStats": {
              "all-4": 11,
              "download-manager": 6,
              "spikes": 77,
              "project-d": 76,
              "bonfire-firebase-sample": 13,
              "novoda": 1
            },
            "externalRepositoriesContributions": 184
          },
          "lgvalle": {
            "assignedProjectsStats": {"The Times: Scheduled": 223},
            "assignedProjectsContributions": 223,
            "externalRepositoriesStats": {"sqlite-provider": 2, "merlin": 3},
            "externalRepositoriesContributions": 5
          },
          "xrigau": {
            "assignedProjectsStats": {"Creators: Scheduled": 190},
            "assignedProjectsContributions": 190,
            "externalRepositoriesStats": {
              "all-4": 2,
              "sqlite-provider": 5,
              "github-reports": 61,
              "gradle-android-command-plugin": 8,
              "merlin": 3,
              "spikes": 48,
              "aosp.changelog.to": 3,
              "bintray-release": 2,
              "oddschecker-android": 4,
              "soundcloud-creators": 25,
              "novoda": 1
            },
            "externalRepositoriesContributions": 162
          },
          "hhaouat": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {"all-4": 131},
            "externalRepositoriesContributions": 131
          },
          "PaNaVTEC": {
            "assignedProjectsStats": {"All4: Verified": 223, "All4: Scheduled": 223},
            "assignedProjectsContributions": 446,
            "externalRepositoriesStats": {},
            "externalRepositoriesContributions": 0
          },
          "jackSzm": {
            "assignedProjectsStats": {"All4: Verified": 198, "All4: Scheduled": 198},
            "assignedProjectsContributions": 396,
            "externalRepositoriesStats": {},
            "externalRepositoriesContributions": 0
          },
          "Electryc": {
            "assignedProjectsStats": {"OddsChecker: Scheduled": 230},
            "assignedProjectsContributions": 230,
            "externalRepositoriesStats": {"github-reports": 7},
            "externalRepositoriesContributions": 7
          },
          "stefanhoth": {
            "assignedProjectsStats": {},
            "assignedProjectsContributions": 0,
            "externalRepositoriesStats": {
              "all-4": 229,
              "sqlite-provider": 6,
              "project-d-api": 10,
              "spikes": 11,
              "project-d": 89,
              "piriform-ccleaner": 54,
              "oddschecker-ios": 25,
              "oddschecker-android": 62,
              "soundcloud-creators": 19
            },
            "externalRepositoriesContributions": 505
          },
          "eduardb": {
            "assignedProjectsStats": {"The Times: Scheduled": 60, "R \u0026 D: Scheduled": 11},
            "assignedProjectsContributions": 71,
            "externalRepositoriesStats": {
              "sqlite-provider": 6,
              "github-reports": 7,
              "spikes": 5,
              "project-d": 11,
              "oddschecker-android": 8
            },
            "externalRepositoriesContributions": 37
          },
          "ataulm": {
            "assignedProjectsStats": {"All4: Verified": 419, "All4: Scheduled": 419},
            "assignedProjectsContributions": 838,
            "externalRepositoriesStats": {
              "all-4": 21,
              "sqlite-provider": 4,
              "merlin": 24,
              "spikes": 72,
              "project-d": 10,
              "novoda": 6,
              "soundcloud-creators": 51
            },
            "externalRepositoriesContributions": 188
          },
          "JozefCeluch": {
            "assignedProjectsStats": {"OddsChecker: Verified": 64, "OddsChecker: Scheduled": 64},
            "assignedProjectsContributions": 128,
            "externalRepositoriesStats": {"spikes": 36, "piriform-ccleaner": 12},
            "externalRepositoriesContributions": 48
          }
        }
      }]);
  }

}
