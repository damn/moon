(ns clojure.gdx.scene2d.actor.set-opts
  (:require [clojure.gdx.scene2d.actor :refer [add-listener!]]))

(defn set-opts! [actor opts]
  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))
