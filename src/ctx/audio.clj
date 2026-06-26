(ns ctx.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx.audio :as audio]
            [files.internal :as internal]))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (audio/new-sound audio
                            (internal/f files path))])))
