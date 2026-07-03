(ns editor.widget-value.number
  (:require [clojure.edn :as edn]
            [clojure.gdx.text-field.get-text :as get-text]))

(defn f
  [_  widget _schemas]
  (edn/read-string (get-text/f widget)))
