(ns clojure.ctx.skin
  (:require [gdl.files.internal :as internal]
            [gdl.bitmap-font.get-data :refer [get-data]]
            [gdl.bitmap-font-data.enable-markup :refer [enable-markup!]]
            [gdl.file.skin :as skin]
            [gdl.ui.skin :as skn]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (get-data (skn/font skin "default-font")))
    skin))
