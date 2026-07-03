(ns editor.widget-value.string
  (:require [clojure.gdx.text-field.get-text :as get-text]))

(defn f
  [_ widget _schemas]
  (get-text/f widget))
