(ns editor.widget-value.one-to-many
  (:require [clojure.gdx :as gdx]))

(defn f
  [_  widget _schemas]
  (->> (gdx/get-children widget)
       (keep gdx/get-user-object)
       vec))
