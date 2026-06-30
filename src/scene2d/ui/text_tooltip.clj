(ns scene2d.ui.text-tooltip
  (:require [clojure.gdx :as gdx]))

(defn create [tooltip skin]
  (gdx/text-tooltip ^String tooltip ^Skin skin))
