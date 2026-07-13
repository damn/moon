(ns gdx.graphics.g2d.bitmap-font-data
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]))

(defn scale-x [font-data]
  (bitmap-font-data/scaleX font-data))

(defn set-scale! [font-data scale]
  (bitmap-font-data/setScale font-data scale))

(defn set-markup-enabled! [font-data enabled?]
  (bitmap-font-data/set-markupEnabled font-data enabled?))
