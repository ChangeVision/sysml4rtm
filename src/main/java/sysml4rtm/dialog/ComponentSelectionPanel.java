package sysml4rtm.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import sysml4rtm.Messages;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.view.IIconManager;
import com.change_vision.jude.api.inf.view.IViewManager;
import com.change_vision.jude.api.inf.view.IconDescription;

class ComponentSelectionPanel extends JPanel {

	class PackageTreeModel implements TreeModel {

		public Object getChild(Object parent, int index) {
			if (isContainer(parent)) {
				IPackage p = (IPackage) parent;
				INamedElement[] ownedElements = p.getOwnedElements();
				int count = 0;
				for (INamedElement element : ownedElements) {
					if (isContainer(element)) {
						if (count == index)
							return element;
						count++;
					}
				}
			}
			return null;
		}

		public int getChildCount(Object parent) {
			if (isContainer(parent)) {
				IPackage p = (IPackage) parent;
				INamedElement[] elements = p.getOwnedElements();
				int count = 0;
				for (int i = 0; i < elements.length; i++) {
					if (isContainer(elements[i])) {
						count++;
					}
				}
				return count;
			}
			return -1;
		}

		public int getIndexOfChild(Object parent, Object child) {
			if (isContainer(parent)) {
				IPackage p = (IPackage) parent;
				INamedElement[] ownedElements = p.getOwnedElements();
				for (int i = 0; i < ownedElements.length; i++) {
					INamedElement iNamedElement = ownedElements[i];
					if (iNamedElement.equals(child)) {
						return i;
					}
				}
				return -1;
			}
			return 0;
		}

		public Object getRoot() {
			return root;
		}

		public boolean isLeaf(Object node) {
			return node != root && !isContainer(node) && !isRTCBlock(node);
		}

		protected EventListenerList listenerList = new EventListenerList();

		public void addTreeModelListener(TreeModelListener l) {
			listenerList.add(TreeModelListener.class, l);
		}

		public void removeTreeModelListener(TreeModelListener l) {
			listenerList.remove(TreeModelListener.class, l);
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}

	class ComponentListModel implements ListModel {

		private java.util.List<INamedElement> components = new ArrayList<INamedElement>();

		void setParent(IPackage parent) {
			java.util.List<INamedElement> elements = new ArrayList<INamedElement>();
			INamedElement[] ownedElements = parent.getOwnedElements();
			for (INamedElement element : ownedElements) {
				//if (isContainer(element) || isRootComponent(element)) {
				if (isContainer(element)) {
					continue;
				}
				elements.add(element);
			}
			setComponents(elements);
		}

		private void setComponents(java.util.List<INamedElement> newComponents) {
			int oldIndex = components.size();
			components = newComponents;
			int index = components.size();
			if (oldIndex > index) {
				index = oldIndex;
			}
			fireContentsChange(index);
		}

		private void fireContentsChange(int index) {
			for (ListDataListener listDataListener : listenerList.getListeners(ListDataListener.class)) {
				listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, index));
			}
		}

		void add(INamedElement component) {
			//if (isContainer(component) || isRootComponent(component))
			if (isContainer(component))
				return;
			if (!isRTCBlock(component))
				return;
			components.add(component);
			fireContentsChange(components.size());
		}

		void remove(INamedElement component) {
			if (isContainer(component))
				return;
			int oldIndex = components.size();
			components.remove(component);
			fireContentsChange(oldIndex);
		}

		INamedElement[] getComponents() {
			return components.toArray(new INamedElement[0]);
		}

		public Object getElementAt(int index) {
			return components.get(index);
		}

		public int getSize() {
			return components.size();
		}

		void clear() {
			int index = components.size();
			components.clear();
			fireContentsChange(index);
		}

		protected EventListenerList listenerList = new EventListenerList();

		public void addListDataListener(ListDataListener l) {
			listenerList.add(ListDataListener.class, l);
		}

		public void removeListDataListener(ListDataListener l) {
			listenerList.remove(ListDataListener.class, l);
		}
	}

	public JList candidateList;
	public JList choosedList;
	public PackageTree packageTree;

	private IModel root;
	private ComponentListModel candidateModel = new ComponentListModel();
	private ComponentListModel choosedModel = new ComponentListModel();
	private IncludeSubpackagesCheckBox includeSubpackagesCheckbox;
	public FilterText filterText;

	private static final long serialVersionUID = -6365197187809018766L;
	static final String NAME = "component_selection_panel";

	public ComponentSelectionPanel(IModel root) {
		this.root = root;
		setName(NAME);
		GridBagLayout manager = new LeftAlignmentGridBagLayout();
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		setLayout(manager);
		createContents();
	}

	private boolean isRTCBlock(INamedElement candidate){
		return ModelUtils.isRTCBlock(candidate);
	}
	
//	private boolean isComponent(INamedElement candidate) {
//		return AstahAPIUtil.isRtcComponent(candidate);
//	}

	private boolean isRTCBlock(Object target) {
		if(target instanceof INamedElement){
			return isRTCBlock((INamedElement) target);
		}
		return false;
	}

	private void createContents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);

		add(new PackageLabel(), gbc);
		gbc.gridx = 1;
		add(new CandidateLabel(), gbc);
		gbc.gridx = 2;
		add(new Empty(), gbc);
		gbc.gridx = 3;
		add(new SelectedComponentLabel(), gbc);

		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weightx = 1.0d;
		gbc.weighty = 0.8d;

		add(new PackageTreePanel(), gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.5d;
		add(new CandidateListPanel(), gbc);
		gbc.weightx = 0.2d;
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.NONE;
		add(new ChoosedButtonPanel(), gbc);
		gbc.weightx = 0.5d;
		gbc.weighty = 0.8d;
		gbc.gridx = 3;
		gbc.fill = GridBagConstraints.BOTH;
		add(new ChoosedListPanel(), gbc);

		gbc.gridy = 2;
		gbc.gridx = 0;
		add(includeSubpackagesCheckbox = new IncludeSubpackagesCheckBox(), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(new FilterPanel(), gbc);

	}

	public INamedElement[] getChoosedComponents() {
		return choosedModel.getComponents();
	}

	private void addComponent(INamedElement[] choosedComponents, Object selected) {
		INamedElement candidate = (INamedElement) selected;
		for (INamedElement choosed : choosedComponents) {
			if (choosed == candidate) {
				return;
			}
		}
		choosedModel.add(candidate);
		candidateModel.remove(candidate);
	}

	private void removeComponent(Object selected) {
		INamedElement candidate = (INamedElement) selected;
		choosedModel.remove(candidate);
		candidateModel.add(candidate);
	}

	private IPackage getCandidates(TreePath selectionPath) {
		if (selectionPath == null)
			return null;
		Object candidates = selectionPath.getLastPathComponent();
		if (isContainer(candidates)) {
			return (IPackage) candidates;
		}
		return null;
	}

	private void traverseCandidates(IPackage candidates) {
		for (INamedElement candidate : candidates.getOwnedElements()) {
			if (isContainer(candidate)) {
				traverseCandidates((IPackage) candidate);
				continue;
			}
			if (accept(candidate)) {
				candidateModel.add(candidate);
			}
		}
	}

	private void refreshCandidates() {
		calcurateCandidates();
		removeSelectedFromCandidates();
	}

	private void removeSelectedFromCandidates() {
		INamedElement[] selectedValues = choosedModel.getComponents();
		if (selectedValues == null || selectedValues.length == 0)
			return;
		for (INamedElement selected : selectedValues) {
			candidateModel.remove(selected);
		}
	}

	private void calcurateCandidates() {
		TreePath currentPath = packageTree.getSelectionPath();
		IPackage candidates = getCandidates(currentPath);
		if (candidates == null)
			return;
		if (includeSubpackagesCheckbox.isSelected()) {
			candidateModel.clear();
			traverseCandidates(candidates);
		} else {
			candidateModel.clear();
			for (INamedElement candidate : candidates.getOwnedElements()) {
				if (isContainer(candidate)) {
					continue;
				}
				if (accept(candidate)) {
					candidateModel.add(candidate);
				}
			}
		}
	}

	private boolean accept(INamedElement candidate) {
		if (candidate == null)
			return false;
		String text = filterText.getText();
		if (text == null || text.equals(""))
			return true;
		String candidateName = candidate.getName();
		if (candidateName == null)
			return false;
		if (candidate.getName() == null)
			return false;
		return candidate.getName().startsWith(text);
	}

	private boolean isContainer(Object candidate) {
		return candidate instanceof IPackage;
	}

	class PackageLabel extends JLabel {
		private static final long serialVersionUID = 1323776174293873035L;
		static final String NAME = "package";

		PackageLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}

	}

	class CandidateLabel extends JLabel {
		private static final long serialVersionUID = 1323776174293873035L;
		static final String NAME = "candidate";

		CandidateLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}

	}

	class SelectedComponentLabel extends JLabel {
		private static final long serialVersionUID = 1323776174293873035L;
		static final String NAME = "selected_component";

		SelectedComponentLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}
	}

	class ChoosedButtonPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 5746740308364988439L;
		static final String NAME = "choosed_button";

		ChoosedButtonPanel() {
			super(new GridLayout(0, 1, 1, 2));
			setName(NAME);
			int[] types = new int[] { ArrowButton.STRONG_RIGHT, ArrowButton.RIGHT, ArrowButton.LEFT,
					ArrowButton.STRONG_LEFT };
			for (int i = 0; i < types.length; i++) {
				JButton btn = new ArrowButton(types[i]);
				btn.addActionListener(this);
				add(btn);
			}
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof ArrowButton) {
				ArrowButton arrow = (ArrowButton) source;
				INamedElement[] components = candidateModel.getComponents();
				INamedElement[] choosedComponents = choosedModel.getComponents();
				switch (arrow.type) {
				case ArrowButton.STRONG_RIGHT:
					for (INamedElement candidate : components) {
						addComponent(choosedComponents, candidate);
					}
					break;
				case ArrowButton.RIGHT:
					if (candidateModel.getComponents().length == 0)
						return;
					for (Object selected : candidateList.getSelectedValues()) {
						addComponent(choosedComponents, selected);
					}
					break;
				case ArrowButton.LEFT:
					if (choosedModel.getComponents().length == 0)
						return;
					for (Object selected : choosedList.getSelectedValues()) {
						removeComponent(selected);
					}
					refreshCandidates();
					break;
				case ArrowButton.STRONG_LEFT:
					choosedModel.clear();
					TreePath currentPath = packageTree.getSelectionPath();
					packageTree.clearSelection();
					packageTree.setSelectionPath(currentPath);
					break;
				default:
					throw new IllegalStateException();
				}
			}
		}

	}

	class PackageTreePanel extends JPanel {
		private static final int WIDTH = 300;
		private static final int HEIGHT = 300;
		private static final long serialVersionUID = -6654919743339539202L;
		static final String NAME = "pacakge_tree";

		PackageTreePanel() {
			setName(NAME);
			setLayout(new BorderLayout());
			JScrollPane pane = new JScrollPane(packageTree = new PackageTree());
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
			add(pane);
		}
	}

	class PackageTree extends JTree {
		private static final long serialVersionUID = -6654919743339539202L;
		static final String NAME = "package_tree";

		PackageTree() {
			setName(NAME);
			setModel(new PackageTreeModel());
			addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					refreshCandidates();
				}
			});
			setCellRenderer(new PackageTreeCellRenderer());
		}
	}

	class PackageTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 424593035381251161L;

		@Override
		public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

			this.setEnabled(tree.isEnabled());
			this.setText(stringValue);
			if (sel) {
				setForeground(getTextSelectionColor());
			} else {
				setForeground(getTextNonSelectionColor());
			}

			if (leaf) {
				setLabelIcon(getLeafIcon());
			} else if (expanded) {
				setLabelIcon(getOpenIcon());
			} else {
				setLabelIcon(getClosedIcon());
			}

			if (value instanceof IPackage) {
				if (value instanceof IModel) {
					Icon icon = getIcon(IconDescription.PROJECT);
					setLabelIcon(icon);
				} else {
					Icon icon = getIcon(IconDescription.PACKAGE);
					setLabelIcon(icon);
				}
			}
			selected = sel;
			return this;
		}

		private synchronized void setLabelIcon(Icon icon) {
			this.setIcon(icon);
		}
		
	    public Icon getIcon(IconDescription desc) {
	        try {
	            ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
	            IViewManager viewManager = projectAccessor.getViewManager();
	            IIconManager iconManager = viewManager.getIconManager();
	            return iconManager.getIcon(desc);
	        } catch (InvalidUsingException ue) {
	            return new ImageIcon();
	        } catch (Exception e) {
	            if (e.toString().equals("java.lang.reflect.UndeclaredThrowableException")) {
	                return new ImageIcon();
	            } else {
	                throw new ApplicationException(e);
	            }
	        } catch (Throwable e) {
	            throw new ApplicationException(e);
	        }
	    }
	}

	class CandidateListPanel extends JPanel {
		private static final int WIDTH = 100;
		private static final int HEIGHT = 300;
		private static final long serialVersionUID = -4153879365928611608L;
		static final String NAME = "candidate_list";

		CandidateListPanel() {
			setName(NAME);
			setLayout(new BorderLayout());
			JScrollPane pane = new JScrollPane(candidateList = new CandidateList());
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
			add(pane);
		}
	}

	@SuppressWarnings("rawtypes")
	class CandidateList extends JList {
		private static final long serialVersionUID = -1625367646988232425L;
		static final String NAME = "candidate_list";

		@SuppressWarnings("unchecked")
		CandidateList() {
			setName(NAME);
			setModel(candidateModel);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() > 1) {
						INamedElement[] choosedComponents = choosedModel.getComponents();
						for (Object selected : candidateList.getSelectedValues()) {
							addComponent(choosedComponents, selected);
						}
					}
				}
			});
		}
	}

	class ChoosedListPanel extends JPanel {
		private static final long serialVersionUID = 2924700103623965967L;
		private static final int WIDTH = 100;
		private static final int HEIGHT = 300;
		static final String NAME = "choose_list";

		ChoosedListPanel() {
			setName(NAME);
			setLayout(new BorderLayout());
			JScrollPane pane = new JScrollPane(choosedList = new ChoosedList());
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
			add(pane);
		}
	}

	@SuppressWarnings("rawtypes")
	class ChoosedList extends JList {
		private static final long serialVersionUID = 7600101339505538076L;
		static final String NAME = "choose_list";

		ChoosedList() {
			setName(NAME);
			setModel(choosedModel);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() > 1) {
						for (Object selected : choosedList.getSelectedValues()) {
							removeComponent(selected);
						}
						refreshCandidates();
						
					}
				}
			});
		}
	}

	class IncludeSubpackagesCheckBox extends JCheckBox {
		private static final long serialVersionUID = 206789418481962920L;
		static final String NAME = "include_subpackges";

		IncludeSubpackagesCheckBox() {
			setName(NAME);
			setText(Messages.getMessage(NAME));

			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					refreshCandidates();
				}
			});
		}
	}

	class FilterPanel extends JPanel {
		private static final long serialVersionUID = 269301584702946888L;
		static final String NAME = "filter";

		FilterPanel() {
			setName(NAME);
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new FilterLabel(), gbc);
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.BOTH;
			add(filterText = new FilterText(), gbc);
		}

	}

	class FilterLabel extends JLabel {
		private static final long serialVersionUID = -6427806826312155579L;
		static final String NAME = "filter";

		FilterLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}
	}

	class FilterText extends JTextField {
		private static final int WIDTH = 200;
		private static final int HEIGHT = 20;
		private static final long serialVersionUID = -504665346523907697L;
		static final String NAME = "filter";

		FilterText() {
			setName(NAME);
			getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) {
					refreshCandidates();
				}

				public void insertUpdate(DocumentEvent e) {
					refreshCandidates();
				}

				public void changedUpdate(DocumentEvent e) {
					refreshCandidates();
				}
			});
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
		}
	}

	@SuppressWarnings("rawtypes")
	class ItemListModel extends AbstractListModel {
		private static final long serialVersionUID = 8572211313721316561L;
		Component[] listarray;

		void set(List<Component> list) {
			listarray = list.toArray(new Component[] {});
		}

		public Object getElementAt(int idx) {
			return listarray[idx];
		}

		public int getSize() {
			return listarray.length;
		}
	}

}
