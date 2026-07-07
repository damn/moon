(ns clojure.scene2d-actor
  (:require
            [clojure.new-actor]))

(defn f
  [{:keys [act! draw!]}]
  (clojure.new-actor/f (or act!
                   (fn [_actor _delta]))
               (or draw!
                   (fn [_actor _batch _parent-alpha]))))
