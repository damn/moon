(ns clojure.moon-start
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.gdx-application :as gdx-application]
            [clojure.listener :as listener]
            [clojure.use-glfw-async :as use-glfw-async])
  (:gen-class))

(defn -main []
  (use-glfw-async/f)
  (gdx-application/f!
   {:listener (listener/f (edn-resource "config/listener.edn"))
    :title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60}))
