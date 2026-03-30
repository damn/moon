(ns moon.actor)

(defprotocol Actor
  (add-listener! [_ listener])
  (user-object [_])
  (stage [_])
  (set-name! [_ name])
  (set-position! [_ [x y]])
  (set-visible! [_ visible?])
  (visible? [_])
  (remove! [_])
  (parent [_]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn find-ancestor
  [actor clazz]
  (if-let [parent (parent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
