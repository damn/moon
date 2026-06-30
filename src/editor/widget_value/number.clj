(ns editor.widget-value.number
  (:require [clojure.edn :as edn]
            [clojure.gdx :as gdx]))

(defn f
  [_  widget _schemas]
  (edn/read-string (gdx/text-field-get-text widget)))
