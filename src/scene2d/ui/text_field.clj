(ns scene2d.ui.text-field
  (:require [clojure.gdx :as gdx]))

(defn create [text skin]
  (gdx/text-field ^String text ^Skin skin))
