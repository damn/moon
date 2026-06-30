(ns editor.widget-value.default
  (:require [clojure.gdx :as gdx]))

(defn f
  [_  widget _schemas]
  ((gdx/get-user-object widget) 1))
