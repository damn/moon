(ns editor.create-widget.boolean
  (:require [clojure.gdx :as gdx]))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (doto (gdx/check-box "" ^Skin skin)
    (gdx/check-box-set-checked! checked?)))
