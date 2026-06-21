(ns clojure.ctx.skin
  (:require [clojure.files :as files]
            [clojure.bitmap-font.get-data :refer [get-data]]
            [clojure.bitmap-font-data.enable-markup :refer [enable-markup!]]
            [clojure.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/create (files/internal files "skin/uiskin.json"))]
    (enable-markup! (get-data (skin/font skin "default-font")))
    skin))
