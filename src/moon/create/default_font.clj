(ns moon.create.default-font
  (:require [clj.api.com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [gdl.files :as files]
            [gdl.graphics.bitmap-font :as font]
            [gdl.graphics.freetype :as freetype]))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (doto (freetype/generate-font file-handle
                                {:size (* size quality-scaling)
                                 :min-filter texture.filter/linear
                                 :mag-filter texture.filter/linear})
    (font/set-scale! (/ quality-scaling))
    (font/enable-markup! enable-markup?)
    (font/use-integer-positions! use-integer-positions?)))

(defn do!
  [{:keys [ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font (generate-font (files/internal files path)
                                              params)))
