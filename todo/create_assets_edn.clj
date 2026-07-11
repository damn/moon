(comment

 (spit "config/sounds.edn"
       (vec (clojure.file/list-files "wav/" ".wav")))


 )

(ns clojure.file
  (:require [clojure.java.io :as io]
            [clojure.java.io.file :as file]
            [clojure.string :as str]))

(defn list-files [folder extension]
  (->> folder
       io/file
       file/list-files
       (filter file/file?)
       (filter #(str/ends-with? (file/get-name %) extension))
       (map file/get-name)))
