(ns editor.widget-value.boolean
  (:require [clojure.gdx :as gdx]))

(defn f
  [_ widget _schemas]
  (gdx/check-box-is-checked? widget))
