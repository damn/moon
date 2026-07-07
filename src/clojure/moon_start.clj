(ns clojure.moon-start
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.listener :as listener]
            [clojure.lwjgl3-application :as lwjgl3-application])
  (:gen-class))

(defn -main []
  (lwjgl3-application/f!
   {:title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60}
   (listener/f (edn-resource "config/listener.edn"))))
