(ns clojure.ctx.skin
  (:require [clojure.files.internal :as internal]
            [clojure.bitmap-font.get-data :refer [get-data]]
            [clojure.bitmap-font-data.enable-markup :refer [enable-markup!]]
            [clojure.file.skin :as skin]
            [clojure.ui.skin :as skn]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (get-data (skn/font skin "default-font")))
    skin))
