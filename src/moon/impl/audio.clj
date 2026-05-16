(ns moon.impl.audio
  (:require [clojure.audio :as audio]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.gdx.app :as app]
            [clojure.gdx.files :as files]))

(defn create
  [{:keys [ctx/app]}]
  (into {}
        (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
          [sound-name
           (audio/new-sound (app/audio app)
                            (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))])))
