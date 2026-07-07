(ns clojure.levelgen-test-start
  (:require [clojure.levelgen-test-application :as levelgen-test-application]
            [clojure.tmx :as tmx]
            [clojure.uf-caves :as uf-caves]
            [clojure.modules :as modules]))

(defn -main []
  (levelgen-test-application/start!
   {:lwjgl-app-config {:title "Levelgen Test"
                       :windowed-mode {:width 1440 :height 900}
                       :foreground-fps 60}
    :initial-level-fn uf-caves/create
    :level-fns [tmx/vampire
                uf-caves/create
                modules/create]
    :ui-viewport-width 1440
    :ui-viewport-height 900
    :world-viewport-width 1440
    :world-viewport-height 900
    :tile-size 48
    :ui-skin-path "skin/uiskin.json"
    :textures-config {:folder "resources/"
                      :extensions #{"png" "bmp"}}
    :zoom-speed 0.1
    :camera-movement-speed 1}))
