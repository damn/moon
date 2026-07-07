(ns clojure.tick-entities
  (:require [clojure.stage :as stage]
            [clojure.ctx-do :refer [do!]]
            [clojure.tick-component :refer [tick-component]]
            [clojure.throwable :as throwable]
            [clojure.error-window :as error-window]))

(defn f
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (do! ctx
        (mapcat (fn [eid]
                  (mapcat (fn [component]
                            (try (tick-component ctx eid component)
                                 (catch Throwable t
                                   (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                          @eid))
                active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                  (error-window/create
                   {:skin skin
                    :throwable t}))))
  ctx)
