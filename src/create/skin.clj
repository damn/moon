(ns create.skin
  (:require [clojure.files :as files]
            [clojure.scenes.scene2d.ui.skin :as skin]
            [clojure.bitmap-font.enable-markup :as enable-markup]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/create (files/internal files "skin/uiskin.json"))]
    (-> skin
        (skin/font "default-font")
        enable-markup/f!)
    skin))
