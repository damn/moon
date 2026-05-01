(ns moon.application.create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Application)))

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/audio (into {}
                              (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                [sound-name
                                 (.newSound (.getAudio app) (.internal (.getFiles app) (format "sounds/%s.wav" sound-name)))]))))
