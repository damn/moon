(ns gdl.toggle-visible
  (:require [gdl.set-visible :refer [set-visible!]]
            [gdl.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
