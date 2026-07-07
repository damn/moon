(ns clojure.ctx-skin
  (:require [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.files :as files]
            [clojure.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/get-font "default-font")
        bitmap-font/get-data
        (bitmap-font-data/set-markup-enabled! true))
    skin))
