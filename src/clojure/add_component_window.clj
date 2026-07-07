(ns clojure.add-component-window
  (:require [clojure.window :as gdx-window]
            [clojure.actor :as actor]
            [clojure.event :as event]
            [clojure.layout :as layout]
            [clojure.build-widget :as build-widget]
            [clojure.widget-value :as widget-value]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]
            [clojure.add-close-button :as add-close-button]
            [clojure.ui-window :as window]
            [clojure.default-value :refer [default-value]]
            [clojure.schemas-map-keys :refer [map-keys]]
            [clojure.optional :refer [optional?]]))

(defn f
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (doto (window/create
                      {:title "Choose"
                       :skin skin
                       :table/cell-defaults {:pad 5}})
                 (add-close-button/f! skin)
                 (gdx-window/set-modal! true))
        remaining-ks (sort (remove (set (keys (widget-value/f schema map-widget-table schemas)))
                                   (map-keys schemas schema)))]
    (add-rows!
     window
     (for [k remaining-ks]
       [{:actor (doto (text-button/create
                       {:skin skin
                        :text (name k)})
                  (actor/add-listener! (change-listener/create
                                   (fn [event _actor]
                                     (actor/remove! window)
                                     (let [ctx (:stage/ctx (event/get-stage event))]
                                       (add-rows! map-widget-table [((:ctx/create-component-row ctx)
                                                                      {:skin skin
                                                                       :editor-widget (build-widget/f ctx
                                                                                                      (get schemas k)
                                                                                                      k
                                                                                                      (default-value schemas k))
                                                                       :k k
                                                                       :display-remove-component-button? (optional? schemas schema k)
                                                                       :table map-widget-table})])
                                       ((:ctx/rebuild-editor-window! ctx) ctx)))))
                  )}]))
    (layout/pack! window)
    window))
