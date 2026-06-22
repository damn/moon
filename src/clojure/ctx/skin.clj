(ns clojure.ctx.skin
  (:require [gdl.files.internal :as internal]
            [gdl.get-data :refer [get-data]]
            [gdl.enable-markup :refer [enable-markup!]]
            [gdl.file.skin :as skin]
            [gdl.skin :as skn]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (get-data (skn/font skin "default-font")))
    skin))
