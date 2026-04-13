(ns moon.create.default-font
  (:require [gdl.files :as files]
            [gdl.graphics.freetype :as freetype]))

(defn do!
  [{:keys [ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font (freetype/generate-font (files/internal files path) params)))
