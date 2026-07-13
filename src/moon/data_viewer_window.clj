(ns moon.data-viewer-window
  (:require [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.actor :as actor]
            [gdx.stage :as stage]
            [gdx.change-listener :as change-listener]))

(defn label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->actor (fn [v skin]
                   (if (map? v)
                     (doto (text-button/create "Map" skin)
                       (actor/add-listener! (change-listener/create
                                            (fn [_event actor]
                                              (stage/add-actor! (actor/get-stage actor)
                                                                (create
                                                                 {:title "title"
                                                                  :data v
                                                                  :width 500
                                                                  :height 500
                                                                  :skin skin}))))))
                     (label/create (cond
                                  (or (keyword? v)
                                      (number? v)
                                      (boolean? v)
                                      (string? v))
                                  (str "[GOLD]" v "[]")

                                  :else
                                  (str (class v)))
                                skin)))
        rows (for [[k v] (sort-by key data)]
               {:label (label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (table/create
                           {:table/rows (for [{:keys [label actor]} rows]
                                           [{:actor (label/create label skin)}
                                            {:actor actor}])})
        scroll-pane-cell {:actor (scroll-pane/create
                                  (table/create {:table/cell-defaults {:pad 1}
                                                 :table/rows [[scroll-pane-table]]})
                                  skin)
                          :width width
                          :height 800}]
    (window/create {:title title
                    :skin skin
                    :table/rows [[scroll-pane-cell]]
                    :window/add-close-button? true})))
