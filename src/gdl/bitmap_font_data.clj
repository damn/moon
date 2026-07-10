(ns gdl.bitmap-font-data
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-bitmap-font-data]))

(defn get-scale-x [& args]
  (apply bitmap-font-bitmap-font-data/scaleX args))

(defn set-markup-enabled! [& args]
  (apply bitmap-font-bitmap-font-data/set-markupEnabled args))

(defn set-scale! [& args]
  (apply bitmap-font-bitmap-font-data/setScale args))
