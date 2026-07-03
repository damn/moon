(ns editor.widget-value.boolean
  (:require [clojure.gdx.checkbox.is-checked? :as is-checked?]))

(defn f
  [_ widget _schemas]
  (is-checked?/f widget))
