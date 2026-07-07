(ns clojure.with-window-close
  (:require [clojure.stage :as stage]
            [clojure.window :as window]
            [clojure.actor :as actor]
            [clojure.throwable :as throwable]
            [clojure.error-window :as error-window]
            [clojure.find-ancestor :refer [find-ancestor]]
            [clojure.scene2d-stage :refer [set-ctx!]]))

(defn f [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (actor/get-stage actor)]
       (set-ctx! stage new-ctx))
     (actor/remove! (find-ancestor actor (partial instance? window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! stage
                    (error-window/create
                     {:type :ui/error-window
                      :skin skin
                      :throwable t}))))))
