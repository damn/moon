(ns ctx.audio
  (:require [clojure.edn :as edn]
            [com.badlogic.gdx.audio :as audio]
            [com.badlogic.gdx.files :as files]
            [clojure.java.io :as io]))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (audio/new-sound audio
                            (files/internal files path))])))
