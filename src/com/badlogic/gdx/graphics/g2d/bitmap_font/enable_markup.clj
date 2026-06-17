(ns com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font.get-data :refer [get-data]]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data.enable-markup :refer [enable-markup!]]))

(defn f! [font]
  (enable-markup! (get-data font)))
