(ns moon.create.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Audio
                             Files)))

(defn step
  [{:keys [ctx/audio
           ctx/files]
    :as ctx}]
  (assoc ctx :ctx/audio
         (into {}
               (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                 [sound-name
                  (Audio/.newSound audio (Files/.internal files (format "sounds/%s.wav" sound-name)))]))))
