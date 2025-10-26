(ns moon.ui.editor.property
  (:require [clojure.pprint :refer [pprint]]))

(defn image [{:keys [entity/image entity/animation]}]
  (or image
      (first (:animation/frames animation))))

(defn tooltip [property]
  (binding [*print-level* 2]
    (with-out-str
     (pprint property))))
