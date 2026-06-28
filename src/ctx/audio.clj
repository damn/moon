(ns ctx.audio
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [files.internal :as internal])
  (:import (com.badlogic.gdx Audio)))

(defn step
  [{:keys [ctx/audio
           ctx/files]}]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (.newSound ^Audio audio
                      (internal/f files path))])))
