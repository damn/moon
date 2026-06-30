(ns editor.widget-value.string
  (:require [clojure.gdx :as gdx]))

(defn f
  [_ widget _schemas]
  (gdx/text-field-get-text widget))
