(ns editor.widget-value.number
  (:require [clojure.edn :as edn]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]))

(defn f
  [_  widget _schemas]
  (edn/read-string (text-field/get-text widget)))
