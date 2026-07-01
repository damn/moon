(ns ctx.audio
  (:require [clojure.edn :as edn]
            [clojure.gdx.audio.new-sound :as new-sound]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Files)))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (new-sound/f audio
                        (Files/.internal files path))])))
