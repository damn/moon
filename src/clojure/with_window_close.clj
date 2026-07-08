(ns clojure.with-window-close
  (:require
            [clojure.scene2d.actor.get-stage]
            [clojure.scene2d.actor.remove-actor] [clojure.stage :as stage]
            [clojure.window :as window]
            [clojure.throwable :as throwable]
            [clojure.error-window :as error-window]
            [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.set-ctx :as set-ctx]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (clojure.scene2d.actor.get-stage/f actor)]
       (set-ctx/f stage new-ctx))
     (clojure.scene2d.actor.remove-actor/f (find-ancestor actor (partial instance? window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! stage
                    (error-window/create
                     {:type :ui/error-window
                      :skin skin
                      :throwable t}))))))
